<?php
/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Illustrates how to handle reports where certain dates within the range
 * are missing, due to there being no impression data.
 *
 * Tags: accounts.reports.generate
 */
class FillMissingDatesInReport {
  /**
   * Retrieves a report for the specified ad client, and fills missing data.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $adClientId string the ad client ID on which to run the report.
   */
  public static function run($service, $accountId, $adClientId) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Running report for ad client %s, and filling missing dates\n",
        $adClientId);
    print $separator;

    $startDate = 'today-7d';
    $endDate = 'today-1d';

    $optParams = array(
      'metric' => array('CLICKS', 'EARNINGS'),
      'dimension' => 'DATE',
      'sort' => '+DATE',
      'filter' => array(
        'AD_CLIENT_ID==' . $adClientId
      )
    );

    // Run report.
    $report = $service->accounts_reports->generate($accountId, $startDate,
        $endDate, $optParams);

    if (isset($report) && isset($report['rows'])) {
      $rows = self::fillMissingDates($report);

      // Display headers.
      foreach($report['headers'] as $header) {
        printf('%25s', $header['name']);
      }
      print "\n";

      // Display results.
      foreach($rows as $row) {
        foreach($row as $column) {
          printf('%25s', $column);
        }
        print "\n";
      }
    } else {
      print "No rows returned.\n";
    }

    print "\n";
  }

  /**
   * Fills in missing date ranges from a report. This is needed because null
   * data for a given period causes that reporting row to be ommitted, rather
   * than set to zero (or the appropriate missing value for the metric in
   * question).
   *
   * NOTE: This code assumes you have a single dimension in your report, and
   * that the dimension is either DATE or MONTH. The number of metrics is not
   * relevant.
   */
  public static function fillMissingDates($report) {
    $fullDateFormat = "Y-m-d";
    $monthDateFormat = "Y-m";

    $startDate = DateTimeImmutable::createFromFormat($fullDateFormat,
        $report['startDate']);
    $endDate = DateTimeImmutable::createFromFormat($fullDateFormat,
        $report['endDate']);

    // Check if the results fit the requirements for this method.
    if (empty($report['headers'])) {
      throw new Exception('No headers defined in report results.');
    }

    if (count($report['headers']) < 2 ||
        $report['headers'][0]['type'] != 'DIMENSION') {
      throw new Exception('Insufficient dimensions and metrics defined.');
    }

    if ($report['headers'][1]['type'] == 'DIMENSION') {
      throw new Exception('Only one dimension allowed.');
    }

    $dateFormat = '';
    $date = null;
    $increment = '';
    // Adjust output format and start date according to time period.
    if ($report['headers'][0]['name'] == 'DATE') {
      $dateFormat = $fullDateFormat;
      $date = $startDate;
      $increment = '+1 day';
    } else if ($report['headers'][0]['name'] == 'MONTH') {
      $dateFormat = $monthDateFormat;
      $date = $startDate->modify('first day of this month');
      $increment = 'first day of next month';
    } else {
      throw new Exception('Results require a DATE or MONTH dimension.');
    }

    $processedData = [];
    $reportRowPos = 0;

    while ($date < $endDate) {
      $rowDate = null;
      $currentRow = [];
      // If we haven't haven't reached the end of the response data yet.
      if (!empty($report['rows']) &&
          count($report['rows']) > $reportRowPos) {
        // Get current row of report and parse the date from it.
        $currentRow = $report['rows'][$reportRowPos];
        $rowDate = DateTimeImmutable::createFromFormat($dateFormat,
            $currentRow[0]);
        // Normalize date if using MONTH dimension.
        if ($report['headers'][0]['name'] == 'MONTH') {
          $rowDate = $rowDate->modify('first day of this month');
        }
      }

      // Is there an entry for this date?
      if (!is_null($rowDate) && $date == $rowDate) {
        $processedData[] = $currentRow;
        $reportRowPos++;
      } else {
        // Generate row with empty data for this date.
        $newRow = [];
        $newRow[] = $date->format($dateFormat);
        $numHeaders = count($report['headers']);
        for ($i = 1; $i < $numHeaders; $i++) {
          $newRow[] = 'no data';
        }
        // Append generated row to processed data.
        $processedData[] = $newRow;
      }

      // Increment date accordingly.
      $date = $date->modify($increment);
    }

    return $processedData;
  }
}
