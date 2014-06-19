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
 * Retrieves two reports and collates data between them, taking into account any
 * missing categories. For example, if a platform is missing from one of the
 * requests in this sample, metric values will be filled in automatically with
 * zeroes.
 *
 * Tags: reports.generate
 */
class CollateReportData {
  /**
   * Retrieves two reports and collates data between them.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $adClientId string the ad client ID on which to run the report.
   */
  public static function run($service, $accountId, $adClientId) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Running reports for ad client %s, and collating data\n",
        $adClientId);
    print $separator;

    $optParams = array(
      'metric' => array('CLICKS', 'EARNINGS'),
      'dimension' => array('PLATFORM_TYPE_CODE', 'PLATFORM_TYPE_NAME'),
      // Sort by ascending PLATFORM_TYPE_CODE.
      'sort' => '+PLATFORM_TYPE_CODE',
      'filter' => array(
        'AD_CLIENT_ID==' . $adClientId
      )
    );

    // The first report is for last week, i.e., the 7-day period ending
    // yesterday.
    $lastWeekReport = $service->accounts_reports->generate($accountId,
        'today-7d', 'today-1d', $optParams);
    // The second report is for the previous week, i.e., the 7-day period
    // ending eight days ago.
    $prevWeekReport = $service->accounts_reports->generate($accountId,
        'today-14d', 'today-8d', $optParams);

    $responses = array($lastWeekReport, $prevWeekReport);

    // Create new objects for storing the row data.
    $lastRows = array();
    $prevRows = array();

    // Add existing data to new lists.
    if (isset($lastWeekReport) && !empty($lastWeekReport['rows'])) {
      $lastRows = $lastWeekReport['rows'];
    }
    if (isset($prevWeekReport) && !empty($prevWeekReport['rows'])) {
      $prevRows = $prevWeekReport['rows'];
    }

    // Compile complete set of platforms and platform codes across both report
    // responses.
    $platformNames = array();
    foreach($responses as $response) {
      if (isset($response) && !empty($response['rows'])) {
        foreach($response['rows'] as $row) {
          $platformNames[$row[0]] = $row[1];
        }
      }
    }
    $platforms = array_keys($platformNames);

    // How many metrics have we got?
    $metrics = 0;
    if (count($lastRows) > 0) {
      // Subtracting 2 to skip headers for dimensions.
      $metrics = count($lastRows[0]) - 2;
    } else if (count($prevRows) > 0) {
      // Subtracting 2 to skip headers for dimensions.
      $metrics = count($prevRows[0]) - 2;
    }

    // Add missing data to both datasets.
    $datasets = array($lastRows, $prevRows);
    foreach ($datasets as &$dataset) {
      if (count($dataset) < count($platforms)) {
        // Compile list of platforms in this dataset.
        $datasetPlatforms = array();
        foreach ($dataset as $row) {
          $datasetPlatforms[] = $row[0];
        }

        // Compile list of missing platforms in this dataset.
        $missing = array_diff($platforms, $datasetPlatforms);

        // Add data for all missing platforms.
        foreach ($missing as $platform) {
          $newRow = array();
          // Add platform code and name.
          $newRow[] = $platform;
          $newRow[] = $platformNames[$platform];
          // Add metrics.
          for ($i = 0; $i < $metrics; $i++) {
            $newRow[] = '0';
          }
          $dataset[] = $newRow;
        }

        // Sort dataset.
        usort($dataset, array('CollateReportData', 'comparePlatforms'));
      }
    }

    // Display effective date range.
    printf("Results for last week (%s to %s) vs previous week (%s to %s).\n",
        $lastWeekReport['startDate'],
        $lastWeekReport['endDate'],
        $prevWeekReport['startDate'],
        $prevWeekReport['endDate']);

    $numPlatforms = count($platforms);
    // Display collated data.
    for ($i = 0; $i < $numPlatforms; $i++) {
      $lastRow = $datasets[0][$i];
      $prevRow = $datasets[1][$i];
      $platform = $lastRow[0];
      printf("%s:\n", $platformNames[$platform]);
      // Adding 2 to skip headers for dimensions.
      for ($j = 2; $j < $metrics + 2; $j++) {
        printf("  * %f delta (%s last week vs %s in the previous week) on %s\n",
            $lastRow[$j] - $prevRow[$j],
            $lastRow[$j],
            $prevRow[$j],
            $lastWeekReport['headers'][$j]['name']);
      }
    }

    print "\n";
  }

  public static function comparePlatforms($rowA, $rowB) {
    return strnatcmp($rowA[0], $rowB[0]);
  }
}
