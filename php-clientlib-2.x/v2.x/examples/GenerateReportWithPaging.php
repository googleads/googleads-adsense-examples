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
 * This example retrieves a report for the specified ad client.
 *
 * Please only use pagination if your application requires it due to memory or
 * storage constraints. If you need to retrieve more than 5000 rows, please
 * check GenerateReport.java, as due to current limitations you will not be able
 * to use paging for large reports.
 *
 * Tags: accounts.reports.generate
 */
class GenerateReportWithPaging {
  // Maximum number of obtainable rows for paged reports (API limit).
  const ROW_LIMIT = 5000;

  /**
   * This example retrieves a report for the specified ad client.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $adClientId string the ad client ID on which to run the report.
   */
  public static function run($service, $accountId, $adClientId,
      $maxReportPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Running paged report for ad client %s\n", $adClientId);
    print $separator;

    $startDate = 'today-7d';
    $endDate = 'today-1d';

    $optParams = array(
      'metric' => array(
        'PAGE_VIEWS', 'AD_REQUESTS', 'AD_REQUESTS_COVERAGE',
        'CLICKS', 'AD_REQUESTS_CTR', 'COST_PER_CLICK', 'AD_REQUESTS_RPM',
        'EARNINGS'),
      'dimension' => 'DATE',
      'sort' => '+DATE',
      'filter' => array(
        'AD_CLIENT_ID==' . $adClientId
      ),
      'maxResults' => $maxReportPageSize
    );

    // Run first page of report.
    $response = $service->accounts_reports->generate($accountId, $startDate,
        $endDate, $optParams);

    if (!isset($response) || empty($response['rows'])) {
      print "No rows returned.\n";
      return;
    }

    // The first page, so display headers.
    foreach($response['headers'] as $header) {
      printf('%25s', $header['name']);
    }
    print "\n";

    // Display first page results.
    self::displayRows($response['rows']);

    $totalRows = min(intval($response['totalMatchedRows']), self::ROW_LIMIT);
    for ($startIndex = count($response['rows']); $startIndex < $totalRows;
         $startIndex += count($response['rows'])) {
      // Check to see if we're going to go above the limit and get as many
      // results as we can.
      $pageSize = min($maxReportPageSize, $totalRows - $startIndex);

      $optParams['startIndex'] = $startIndex;
      $optParams['maxResults'] = $pageSize;

      // Run next page of report.
      $response = $service->accounts_reports->generate($accountId, $startDate,
          $endDate, $optParams);

      // If the report size changes in between paged requests, the result may be
      // empty.
      if (!isset($response) || empty($response['rows'])) {
        break;
      }

      // Display results.
      self::displayRows($response['rows']);
    }

    print "\n";
  }

  // Displays a list of rows for the report.
  private static function displayRows($rows) {
    foreach($rows as $row) {
      foreach($row as $column) {
        printf('%25s', $column);
      }
      print "\n";
    }
  }
}
