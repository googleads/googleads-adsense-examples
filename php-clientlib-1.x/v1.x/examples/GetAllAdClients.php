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
 * This example gets all ad clients for the specified account.
 *
 * Tags: accounts.adclients.list
 */
class GetAllAdClients {
  /**
   * Gets all ad clients for the specified account.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   * @return array the last page of retrieved ad clients.
   */
  public static function run($service, $accountId, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all ad clients for account %s\n", $accountId);
    print $separator;

    $optParams['maxResults'] = $maxPageSize;

    $pageToken = null;
    $adClients = null;
    do {
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts_adclients->listAccountsAdclients($accountId,
          $optParams);
      if (!empty($result['items'])) {
        $adClients = $result['items'];
        foreach ($adClients as $adClient) {
          printf("Ad client for product \"%s\" with ID \"%s\" was found.\n",
              $adClient['productCode'], $adClient['id']);
        }
        if (isset($result['nextPageToken'])) {
          $pageToken = $result['nextPageToken'];
        }
      } else {
        print "No ad clients found.\n";
      }
    } while ($pageToken);
    print "\n";

    return $adClients;
  }
}
