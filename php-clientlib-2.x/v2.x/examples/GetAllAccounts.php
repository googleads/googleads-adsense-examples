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
 * Gets all accounts for the logged in user.
 *
 * Tags: accounts.list
 */
class GetAllAccounts {
  /**
   * Gets all accounts for the logged in user.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $maxPageSize int the maximum page size to retrieve.
   * @return array the last page of retrieved accounts.
   */
  public static function run($service, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    print "Listing all AdSense accounts\n";
    print $separator;

    $optParams['maxResults'] = $maxPageSize;

    $pageToken = null;
    do {
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts->listAccounts($optParams);
      $accounts = null;
      if (!empty($result['items'])) {
        $accounts = $result['items'];
        foreach ($accounts as $account) {
          printf("Account with ID \"%s\" and name \"%s\" was found.\n",
              $account['id'], $account['name']);
        }
        if (isset($result['nextPageToken'])) {
          $pageToken = $result['nextPageToken'];
        }
      } else {
        print "No accounts found.\n";
      }
    } while ($pageToken);
    print "\n";

    return $accounts;
  }
}
