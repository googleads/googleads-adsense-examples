#!/usr/bin/python
#
# Copyright 2014 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import datetime
import itertools
import random
import unittest

import adsense_util_data_collator

DATE_FORMAT = adsense_util_data_collator.DATE_FORMAT
MONTH_FORMAT = adsense_util_data_collator.MONTH_FORMAT


class TestSequenceFunctions(unittest.TestCase):

  class FakeReport(object):
    """Creates a fake report.

    Args:
      start_date: a string with a date in DATE_FORMAT.
      end_date: a string with a date in DATE_FORMAT.
      time_dimensions: a list of strings containing none or all of "DATE",
        "WEEK" and "MONTH".
      dimensions: a list of strings representing dimensions.
      metrics: a list of strings representing metrics.
      ratios: a list of strings representing metrics that are ratios.
      currencies: a list of strings representing metrics that are currencies.
      dimension_values: a dictionary of dimensions where the values are lists
        of strings.
      row_fill_factor: a number between 0 and 1 (inclusive) that defines the
        number of rows that are not returned, to simulate periods without
        activity.
    """

    def __init__(self, start_date=u'2014-02-28', end_date=u'2014-04-07',
                 time_dimensions=None, dimensions=None, metrics=None,
                 ratios=None, currencies=None, dimension_values=None,
                 row_fill_factor=0.8):
      # Default values for the fake report:
      self.start_date = start_date

      self.end_date = end_date

      self.time_dimensions = (['MONTH'] if time_dimensions is None
                              else time_dimensions)

      self.dimensions = (['PLATFORM_TYPE'] if dimensions is None
                         else dimensions)

      self.metrics = (['CLICKS', 'PAGE_VIEWS'] if metrics is None
                      else metrics)

      self.ratios = (['AD_REQUESTS_CTR', 'AD_REQUESTS_COVERAGE']
                     if ratios is None else ratios)

      self.currencies = (['EARNINGS', 'COST_PER_CLICK'] if currencies is None
                         else currencies)

      self.dimension_values = (
          {'PLATFORM_TYPE': ['Desktop', 'High-end mobile devices']}
          if dimension_values is None else dimension_values)

      # Sort the time dimensions: DATE before WEEK before MONTH.
      self.time_dimensions = [
          dimension for dimension in ['DATE', 'WEEK', 'MONTH']
          if dimension in self.time_dimensions]

      self.row_fill_factor = row_fill_factor

    def generate(self):
      """Generates the fake report.

      Returns:
        The generated report.
      """
      report = {}
      self._fill_dates(report)
      self._fill_headers(report)
      self._fill_rows(report)
      return report

    def _fill_dates(self, report):
      report['startDate'] = self.start_date
      report['endDate'] = self.end_date

    def _fill_headers(self, report):
      report['headers'] = []
      for time_dimension in self.time_dimensions:
        time_dimension_header = {u'type': u'DIMENSION',
                                 u'name': time_dimension}
        report['headers'].append(time_dimension_header)

      for dimension in self.dimensions:
        dimension_header = {u'type': u'DIMENSION', u'name': dimension}
        report['headers'].append(dimension_header)

      for metric in self.metrics:
        metrics_header = {u'type': u'METRIC_TALLY', u'name': metric}
        report['headers'].append(metrics_header)

      for ratio in self.ratios:
        ratios_header = {u'type': u'METRIC_RATIO', u'name': ratio}
        report['headers'].append(ratios_header)

      for currency in self.ratios:
        currencies_header = {u'type': u'METRIC_CURRENCY', u'name': currency}
        report['headers'].append(currencies_header)

    def _fill_rows(self, report):
      # Make sure the resulting report is random but always the same.
      random.seed(0)

      # Fill the rows with data.
      report['rows'] = []
      cursor_date = datetime.datetime.strptime(self.start_date, DATE_FORMAT)
      end_date = datetime.datetime.strptime(self.end_date, DATE_FORMAT)
      while cursor_date <= end_date:
        for combination in itertools.product(*self.dimension_values.values()):
          # The fill factor specifies the ratio of missing rows.
          if random.random() > self.row_fill_factor:
            continue
          row = []
          if 'DATE' in self.time_dimensions:
            row.append(adsense_util_data_collator.date_to_date_st(cursor_date))
          if 'WEEK' in self.time_dimensions:
            row.append(adsense_util_data_collator.date_to_week_st(cursor_date))
          if 'MONTH' in self.time_dimensions:
            row.append(adsense_util_data_collator.date_to_month_st(cursor_date))
          for dimension in combination:
            row.append(dimension)
          for _ in xrange(len(self.metrics)):
            row.append('42')
          for _ in xrange(len(self.ratios)):
            row.append('3.14')
          for _ in xrange(len(self.currencies)):
            row.append('99.95')
          report['rows'].append(row)

        # No time dimensions, we don't need to loop.
        if not self.time_dimensions:
          break

        # If there are time dimensions, increase the cursor and loop.
        if 'DATE' in self.time_dimensions:
          cursor_date += datetime.timedelta(days=1)
        elif 'WEEK' in self.time_dimensions:
          cursor_date += datetime.timedelta(days=7)
        elif 'MONTH' in self.time_dimensions:
          cursor_date = adsense_util_data_collator.increase_month(cursor_date)
          cursor_date = datetime.datetime(
              cursor_date.year, cursor_date.month, 1)

  def setUp(self):
    # Generate the default fake report.
    generatedr = [self.FakeReport().generate()]
    self.data_collator_default = (
        adsense_util_data_collator.DataCollator(generatedr))

  def test_get_header_index(self):
    report = self.data_collator_default.reports[0]
    self.assertEquals(
        self.data_collator_default._get_header_index(report, 'DATE'), -1)
    self.assertEquals(
        self.data_collator_default._get_header_index(report, 'WEEK'), -1)
    self.assertEquals(
        self.data_collator_default._get_header_index(report, 'MONTH'), 0)

  def test_multiple_reports_without_date_filling(self):
    """Tests two contiguous reports."""
    report1 = self.FakeReport(start_date='2013-01-01', end_date='2013-01-03',
                              time_dimensions=['DATE'], dimensions=[])
    report2 = self.FakeReport(start_date='2013-01-04', end_date='2013-01-06',
                              time_dimensions=['DATE'], dimensions=[])
    reports = [report1.generate(), report2.generate()]
    datacollator = adsense_util_data_collator.DataCollator(reports)
    resulting_report = datacollator.collate_data()
    self.assertEquals(len(resulting_report['rows']), 6)

  def test_multiple_reports_with_date_filling(self):
    """Tests two non-contiguous reports."""
    report1 = self.FakeReport(start_date='2013-01-01', end_date='2013-01-03',
                              time_dimensions=['DATE'], dimensions=[])
    report2 = self.FakeReport(start_date='2013-01-05', end_date='2013-01-07',
                              time_dimensions=['DATE'], dimensions=[])
    reports = [report1.generate(), report2.generate()]

    self.assertRaises(ValueError, adsense_util_data_collator.DataCollator,
                      reports)

  def test_same_row_different_reports(self):
    """Tests for duplicated rows.

    Only the first row should be returned.
    """
    # 2013-01-03 will be duplicated.
    report1 = self.FakeReport(start_date='2013-01-01', end_date='2013-01-03',
                              time_dimensions=['DATE'], dimensions=[])
    report2 = self.FakeReport(start_date='2013-01-03', end_date='2013-01-04',
                              time_dimensions=['DATE'], dimensions=[])
    reports = [report1.generate(), report2.generate()]
    datacollator = adsense_util_data_collator.DataCollator(reports)
    resulting_report = datacollator.collate_data()
    self.assertEquals(len(resulting_report['rows']), 4)

  def test_multiple_empty_reports_with_date_filling(self):
    """Tests an empty report just with one time dimension."""
    report1 = self.FakeReport(start_date='2013-01-01', end_date='2013-01-03',
                              time_dimensions=['DATE'], row_fill_factor=0,
                              dimensions=[])
    report2 = self.FakeReport(start_date='2013-01-04', end_date='2013-01-06',
                              time_dimensions=['DATE'], row_fill_factor=0,
                              dimensions=[])
    reports = [report1.generate(), report2.generate()]
    datacollator = adsense_util_data_collator.DataCollator(reports)
    resulting_report = datacollator.collate_data()

    self.assertEquals(len(resulting_report['rows']), 6)

  def test_multiple_empty_reports_with_date_filling_multiple_dimensions(self):
    """Tests an empty report with two dimensions (time and generic).

    The second dimension should be ignored as there are no dimension values.
    """
    dimensions = ['DIM1']
    dimension_values = {
        'DIM1': ['Unused', 'Unused']}
    report1 = self.FakeReport(start_date='2013-01-01',
                              end_date='2013-01-03',
                              time_dimensions=['DATE'],
                              row_fill_factor=0,
                              dimensions=dimensions,
                              dimension_values=dimension_values)
    report2 = self.FakeReport(start_date='2013-01-04',
                              end_date='2013-01-06',
                              time_dimensions=['DATE'],
                              row_fill_factor=0,
                              dimensions=dimensions,
                              dimension_values=dimension_values)
    reports = [report1.generate(), report2.generate()]
    datacollator = adsense_util_data_collator.DataCollator(reports)
    resulting_report = datacollator.collate_data()
    self.assertEquals(len(resulting_report['rows']), 6)

  def test_no_time_dimensions_report(self):
    dimensions = ['DIM1']
    dimension_values = {
        'DIM1': ['Value1', 'Value2']}
    report1 = self.FakeReport(start_date='2013-01-01',
                              end_date='2013-01-03',
                              row_fill_factor=1,
                              dimensions=dimensions,
                              time_dimensions=[],
                              dimension_values=dimension_values).generate()
    datacollator = adsense_util_data_collator.DataCollator([report1])
    resulting_report = datacollator.collate_data()
    self.assertEquals(len(resulting_report['rows']), 2)

  def test_no_reports_error(self):

    self.assertRaises(ValueError, adsense_util_data_collator.DataCollator, [])

  def test_no_headers_error(self):
    report1 = self.FakeReport(start_date='2013-01-01',
                              end_date='2013-01-03',
                              time_dimensions=[],
                              row_fill_factor=0,
                              dimensions=[],
                              metrics=[],
                              ratios=[],
                              currencies=[],
                              dimension_values={}).generate()
    reports = [report1]
    self.assertRaises(ValueError,
                      adsense_util_data_collator.DataCollator,
                      reports)

  def test_no_dimensions_error(self):
    report1 = self.FakeReport(start_date='2013-01-01',
                              end_date='2013-01-03',
                              time_dimensions=[],
                              row_fill_factor=0,
                              dimensions=[],
                              dimension_values={}).generate()
    reports = [report1]
    self.assertRaises(ValueError,
                      adsense_util_data_collator.DataCollator,
                      reports)

  def test_different_reports_error(self):
    date_report = self.FakeReport(start_date='2013-01-01',
                                  end_date='2013-01-03',
                                  time_dimensions=['DATE'],
                                  row_fill_factor=0).generate()
    month_report = self.FakeReport(start_date='2013-01-04',
                                   end_date='2013-01-06',
                                   time_dimensions=['DATE', 'MONTH'],
                                   row_fill_factor=0).generate()
    self.assertRaises(ValueError,
                      adsense_util_data_collator.DataCollator,
                      [date_report, month_report])

  def test_different_reports_error2(self):
    date_report = self.FakeReport(start_date='2013-01-01',
                                  end_date='2013-01-03',
                                  time_dimensions=['DATE'],
                                  row_fill_factor=0).generate()
    month_report = self.FakeReport(start_date='2013-01-04',
                                   end_date='2013-01-06',
                                   time_dimensions=['MONTH'],
                                   row_fill_factor=0).generate()
    self.assertRaises(ValueError,
                      adsense_util_data_collator.DataCollator,
                      [date_report, month_report])

  def test_bad_reports_error(self):
    report1 = self.FakeReport(start_date='2013-01-01',
                              end_date='2013-01-03',
                              time_dimensions=['DATE'],
                              row_fill_factor=0).generate()
    report2 = self.FakeReport(start_date='2013-01-04',
                              end_date='2013-01-06',
                              time_dimensions=['DATE'],
                              row_fill_factor=0).generate()
    report2['headers'][0], report2['headers'][1] = (
        report2['headers'][1], report2['headers'][0])

    self.assertRaises(ValueError,
                      adsense_util_data_collator.DataCollator,
                      [report1, report2])

  def test_increase_month(self):
    increase_month = adsense_util_data_collator.increase_month
    self.assertEquals(increase_month(datetime.datetime(2010, 01, 01)),
                      datetime.datetime(2010, 02, 01))
    self.assertEquals(increase_month(datetime.datetime(2010, 12, 01)),
                      datetime.datetime(2011, 01, 01))
    self.assertEquals(increase_month(datetime.datetime(2010, 12, 31)),
                      datetime.datetime(2011, 01, 31))
    self.assertEquals(increase_month(datetime.datetime(2010, 01, 31)),
                      datetime.datetime(2010, 02, 28))
    # Test leap year.
    self.assertEquals(increase_month(datetime.datetime(2012, 01, 31)),
                      datetime.datetime(2012, 02, 29))
    self.assertEquals(increase_month(datetime.datetime(2010, 11, 30)),
                      datetime.datetime(2010, 12, 30))

  def test_get_all_dimensions_from_report(self):
    report = self.data_collator_default.reports[0]
    dimensions = self.data_collator_default._get_all_dimensions_from_report(
        report)
    self.assertEquals(dimensions, [(0, u'MONTH'), (1, u'PLATFORM_TYPE')])

  def test_create_new_row(self):
    self.data_collator_default.month_index = 0
    february = datetime.datetime(2014, 02, 01)
    dimension = u'High-end mobile devices'
    combination = [dimension]
    new_row = self.data_collator_default._create_new_row(combination, february)
    self.assertIn('2014-02', new_row)
    self.assertIn(dimension, new_row)

  def test_generate_every_dimension_combination(self):
    """Tests the generate_every_combination method for a simple report.

    A report is generated with one dimension with three possible values. The
    list of combinations should have length 1x3.
    """
    dimensions = [u'PLATFORM_TYPE']
    dimension_values = {u'PLATFORM_TYPE': [
        u'Desktop', u'High-end mobile devices', u'Tablets']}
    generatedr = [self.FakeReport(
        dimensions=dimensions, dimension_values=dimension_values).generate()]

    datacollator = adsense_util_data_collator.DataCollator(generatedr)

    # Sets can be used to compare as the combinations are unique.
    self.assertEquals(
        set([x for x in datacollator._generate_every_dimension_combination()]),
        set([(u'High-end mobile devices',), (u'Tablets',), (u'Desktop',)]))

  def test_generate_every_dimension_combination_empty(self):
    """Tests the generate_every_combination method for a simple report.

    A report is generated with one dimension with three possible values. The
    list of combinations should have length 1x3.
    """
    dimensions = [u'PLATFORM_TYPE']
    dimension_values = {u'PLATFORM_TYPE': ['Unused']}
    generatedr = [
        self.FakeReport(dimensions=dimensions,
                        dimension_values=dimension_values,
                        row_fill_factor=0).generate()]

    datacollator = adsense_util_data_collator.DataCollator(generatedr)

    # Sets can be used to compare as the combinations are unique.
    self.assertEquals(
        [x for x in datacollator._generate_every_dimension_combination()], [[]])

  def test_generate_every_dimension_combination_multiple(self):
    """Tests the generate_every_combination method for a complex report.

    A report is generated with two dimensions, one having three values and
    the other one having two. The list of combinations should have length 2x3.
    """
    dimensions = [u'PLATFORM_TYPE', u'ANOTHER_DIMENSION']
    dimension_values = {u'PLATFORM_TYPE': [
        u'Desktop', u'High-end mobile devices', u'Tablets'],
                        u'ANOTHER_DIMENSION': [u'Value1', u'Value2']}
    generatedr = [self.FakeReport(
        dimensions=dimensions, dimension_values=dimension_values).generate()]
    datacollator = adsense_util_data_collator.DataCollator(generatedr)

    expected_multiple_dimensions = [
        (u'Desktop', u'Value1'),
        (u'Desktop', u'Value2'),
        (u'High-end mobile devices', u'Value1'),
        (u'High-end mobile devices', u'Value2'),
        (u'Tablets', u'Value1'),
        (u'Tablets', u'Value2')]

    # Sets can be used to compare as the combinations are unique.
    self.assertEquals(
        set([x for x in datacollator._generate_every_dimension_combination()]),
        set(expected_multiple_dimensions))

  def test_collate_data_simple(self):
    self._run_common_tests_and_get_result(self.data_collator_default)

  def test_collate_data_complex(self):
    reports_multiple_dim_values = {
        'PLATFORM_TYPE': ['Desktop', 'High-end mobile devices', 'Tablets'],
        'ANOTHER_DIMENSION': ['Value1', 'Value2']
        }
    reports_multiple_dim = [self.FakeReport(
        start_date='2014-02-06', end_date='2014-04-07',
        time_dimensions=['MONTH'],
        dimensions=['PLATFORM_TYPE', 'ANOTHER_DIMENSION'],
        dimension_values=reports_multiple_dim_values).generate()]

    collator = adsense_util_data_collator.DataCollator(reports_multiple_dim)

    self._run_common_tests_and_get_result(collator)

  def test_collate_data_complex_week(self):
    reports_multiple_dim_values = {
        'PLATFORM_TYPE': ['Desktop', 'High-end mobile devices', 'Tablets'],
        'ANOTHER_DIMENSION': ['Value1', 'Value2']
        }
    reports_multiple_dim = [self.FakeReport(
        start_date='2014-02-06', end_date='2014-04-07',
        time_dimensions=['MONTH', 'WEEK'],
        dimensions=['PLATFORM_TYPE', 'ANOTHER_DIMENSION'],
        dimension_values=reports_multiple_dim_values).generate()]

    collator = adsense_util_data_collator.DataCollator(reports_multiple_dim)

    # Common test for every collated report.
    result = self._run_common_tests_and_get_result(collator)
    self.assertTrue(collator.week_index != -1)

    # The number of weeks includes 2014-02-24 twice because it runs between
    # months (ends in March).
    number_of_weeks = 10

    different_dim_values = (
        len(reports_multiple_dim_values['PLATFORM_TYPE'])
        * len(reports_multiple_dim_values['ANOTHER_DIMENSION']))
    self.assertEquals(len(result['rows']),
                      number_of_weeks * different_dim_values)

  def _run_common_tests_and_get_result(self, data_collator):
    """Collates data and runs common tests that apply to every result.

    Args:
      data_collator: An instance of DataCollator.

    Returns:
      the resulting report.
    """
    result = data_collator.collate_data()

    # Make sure everything contained in the original reports is in the result.
    number_rows_original = 0
    for report in data_collator.reports:
      number_rows_original += len(report['rows'])
      for row in report['rows']:
        self.assertIn(row, result['rows'])

    # Make sure the number of rows in the result is at least the number of rows
    # in the original reports.
    self.assertTrue(number_rows_original <= len(result['rows']))
    return result

  def test_generator(self):
    self.FakeReport().generate()

if __name__ == '__main__':
  unittest.main()
