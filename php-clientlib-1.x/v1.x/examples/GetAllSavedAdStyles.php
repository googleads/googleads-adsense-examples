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
 * This example gets all saved ad styles for the specified account.
 *
 * Tags: accounts.savedadstyles.list
 */
class GetAllSavedAdStyles {
  /**
   * Gets all saved ad styles for the specified account.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   */
  public static function run($service, $accountId, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all saved ad styles for account %s\n", $accountId);
    print $separator;

    $optParams['maxResults'] = $maxPageSize;

    $pageToken = null;
    $savedAdStyles = null;
    do {
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts_savedadstyles->listAccountsSavedadstyles(
          $accountId, $optParams);
      if (!empty($result['items'])) {
        $savedAdStyles = $result['items'];
        foreach ($savedAdStyles as $savedAdStyle) {
          printf("Saved ad style with name \"%s\" was found.\n",
              $savedAdStyle['name']);
        }
        if (isset($result['nextPageToken'])) {
          $pageToken = $result['nextPageToken'];
        }
      } else {
        print "No saved ad styles found.\n";
      }
    } while ($pageToken);
    print "\n";
  }
}
