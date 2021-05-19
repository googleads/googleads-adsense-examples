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
 * This example gets all saved reports for the specified account.
 *
 * Tags: accounts.reports.saved.list
 */
class GetAllSavedReports {
  /**
   * Gets all saved reports for the specified account.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   * @return array the last page of saved reports.
   */
  public static function run($service, $accountId, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all saved reports for account %s\n", $accountId);
    print $separator;

    $optParams['pageSize'] = $maxPageSize;

    $pageToken = null;
    $savedReports = null;
    do {
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts_reports_saved->listAccountsReportsSaved(
          $accountId, $optParams);
      if (!empty($result['savedReports'])) {
        $savedReports = $result['savedReports'];
        foreach ($savedReports as $savedReport) {
          printf("Saved report with ID \"%s\" and name \"%s\" was found.\n",
              $savedReport['name'], $savedReport['title']);
        }
        $pageToken = $result['nextPageToken'];
      } else {
        print "No saved reports found.\n";
      }
    } while ($pageToken);
    print "\n";

    return $savedReports;
  }
}
