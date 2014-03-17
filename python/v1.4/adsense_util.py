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

"""Utility class to handle multiple accounts per login.
"""

import copy
import datetime

__author__ = 'jalc@google.com (Jose Alcerreca)'

DATE_FORMAT = '%Y-%m-%d'
MONTH_FORMAT = '%Y-%m'


def get_account_id(service):
  """Gets the AdSense account id, letting the user choose if multiple exist.

  Returns the selected account id.
  """
  account_id = None
  accounts = service.accounts().list().execute()
  if len(accounts['items']) == 1:
    account_id = accounts['items'][0]['id']
  else:
    print 'Multiple accounts were found. Please choose:'
    for i, account in enumerate(accounts['items']):

      print ' %d) %s (%s)' % (i, account['name'], account['id'])
    selection = (raw_input('Please choose number 1-%d>'
                           % (len(accounts['items']))))
    account_id = accounts['items'][int(selection) - 1]['id']
  return account_id


def fill_date_gaps(result):
  """Fills gaps and sorts the result object. Doesn't fill "WEEK" dimension.

  Returns the same structure with dummy rows for non-existing dates.

  >>> result = {'headers': [{'name':'DATE'}, {'name':'EARNINGS'}], 'rows': [],\
 'startDate':'2012-12-29', 'endDate':'2013-01-01'}
  >>> fill_date_gaps(result)
  {'headers': [{'name': 'DATE'}, {'name': 'EARNINGS'}], 'rows': [['2012-12-29'\
, 'N/A'], ['2012-12-30', 'N/A'], ['2012-12-31', 'N/A'], ['2013-01-01', 'N/A']]\
, 'endDate': '2013-01-01', 'startDate': '2012-12-29'}
  """

  date_index = None
  month_index = None
  try:
    date_index = [x['name'] for x in result['headers']].index('DATE')
  except ValueError:
    pass
  try:
    month_index = [x['name'] for x in result['headers']].index('MONTH')
  except ValueError:
    pass

  if date_index is None and month_index is None:
    return result

  # Convert dates
  from_st, to_st = result['startDate'], result['endDate']
  from_date = datetime.datetime.strptime(from_st, DATE_FORMAT)
  to_date = datetime.datetime.strptime(to_st, DATE_FORMAT)

  # Rebuild result.
  result_fill = copy.deepcopy(result)
  result_fill['rows'] = []

  # Days.
  if date_index is not None:
    for i in range((to_date - from_date).days + 1):
      cursor_date = from_date + datetime.timedelta(days=i)
      cursor_st = cursor_date.strftime(DATE_FORMAT)

      new_row = ['N/A' for x in result['headers']]
      new_row[date_index] = cursor_st
      if month_index is not None:
        new_row[month_index] = cursor_date.strftime(MONTH_FORMAT)

      # Get the data from original object.
      if 'rows' in result:
        for row in result['rows']:
          if row[date_index] == cursor_st:
            new_row = row
            break

      result_fill['rows'].append(new_row)

    return result_fill
  # Months.
  months_delta = _months_delta(to_date, from_date)
  if month_index is not None:
    for i in range(months_delta):
      cursor_date = from_date
      for _ in range(i):
        cursor_date = _increase_month(cursor_date)
      cursor_st = cursor_date.strftime(MONTH_FORMAT)

      new_row = ['N/A' for x in result['headers']]
      new_row[month_index] = cursor_st

      # Get the data from original object.
      if 'rows' in result:
        for row in result['rows']:
          if row[month_index] == cursor_st:
            new_row = row
            break

      result_fill['rows'].append(new_row)
    return result_fill


def _months_delta(to_date, from_date):
  """Check how many months, inclusive, between two months.

  Returns the number of months as an integer.

  >>> _months_delta(datetime.datetime(2014,01,01), \
datetime.datetime(2013,12,31))
  2
  >>> _months_delta(datetime.datetime(2014,01,01), \
datetime.datetime(2013,12,01))
  2
  >>> _months_delta(datetime.datetime(2013,12,31), \
datetime.datetime(2013,12,01))
  1
  >>> _months_delta(datetime.datetime(2014,01,01), \
datetime.datetime(2013,01,31))
  13
  >>> _months_delta(datetime.datetime(2014,05,01), \
datetime.datetime(2012,12,31))
  18
  """
  months_delta = (to_date.year - from_date.year) * 12
  return to_date.month - from_date.month + months_delta + 1


def _increase_month(date):
  """Increase a date one month.

  Returns a datetime object.

  >>> _increase_month(datetime.datetime(2014,05,01))
  datetime.datetime(2014, 6, 1, 0, 0)

  >>> _increase_month(datetime.datetime(2014,12,01))
  datetime.datetime(2015, 1, 1, 0, 0)

  """
  if date.month == 12:
    return datetime.datetime(date.year + 1, 1, date.day)
  return datetime.datetime(date.year, date.month + 1, date.day)

if __name__ == "__main__":
    import doctest
    doctest.testmod()
