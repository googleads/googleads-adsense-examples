<?php
/*
 * Copyright 2021 Google LLC
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
 * Retrieves a report for the specified ad client.
 *
 * Tags: accounts.reports.generate
 */
class GenerateReport {
  /**
   * Retrieves a report for the specified ad client.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $adClientId string the ad client ID on which to run the report.
   */
  public static function run($service, $accountId, $adClientId) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Running report for ad client %s\n", $adClientId);
    print $separator;

    $adClientCode = substr($adClientId, strrpos($adClientId, '/') + 1);

    $optParams = array(
      'startDate.year' => 2021,
      'startDate.month' => 3,
      'startDate.day' => 1,
      'endDate.year' => 2021,
      'endDate.month' => 3,
      'endDate.day' => 31,
      'metrics' => array(
        'PAGE_VIEWS', 'AD_REQUESTS', 'AD_REQUESTS_COVERAGE', 'CLICKS',
        'AD_REQUESTS_CTR', 'COST_PER_CLICK', 'AD_REQUESTS_RPM',
        'ESTIMATED_EARNINGS'),
      'dimensions' => 'DATE',
      'orderBy' => '+DATE',
      'filters' => array(
        'AD_CLIENT_ID==' . $adClientCode
      )
    );

    // Run report.
    $report = $service->accounts_reports->generate($accountId, $optParams);

    if (isset($report) && isset($report['rows'])) {
      // Display headers.
      foreach($report['headers'] as $header) {
        printf('%25s', $header['name']);
      }
      print "\n";

      // Display results.
      foreach($report['rows'] as $row) {
        foreach($row['cells'] as $column) {
          printf('%25s', $column['value']);
        }
        print "\n";
      }
    } else {
      print "No rows returned.\n";
    }

    print "\n";
  }
}
