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
 * This example gets all custom channels an ad unit has been added to.
 *
 * Tags: accounts.adunits.customchannels.list
 */
class GetAllCustomChannelsForAdUnit {
  /**
   * Gets all custom channels an ad unit has been added to.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   * @param $adClientId string the ID for the ad client to be used.
   * @param $adUnitId string the ID for the ad unit to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   */
  public static function run($service, $accountId, $adClientId, $adUnitId,
      $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all custom channels for ad unit %s\n", $adUnitId);
    print $separator;

    $optParams['maxResults'] = $maxPageSize;

    $pageToken = null;
    $customChannels = null;
    do {
      $optParams['pageToken'] = $pageToken;

      $customChannelResource = $service->accounts_adunits_customchannels;
      $result = $customChannelResource->listAccountsAdunitsCustomchannels(
          $accountId, $adClientId, $adUnitId, $optParams);
      if (!empty($result['items'])) {
        $customChannels = $result['items'];
        foreach ($customChannels as $customChannel) {
          printf("Custom channel with code \"%s\" and name \"%s\" was found.\n",
              $customChannel['code'], $customChannel['name']);
        }
        if (isset($result['nextPageToken'])) {
          $pageToken = $result['nextPageToken'];
        }
      } else {
        print "No custom channels found.\n";
      }
    } while ($pageToken);
    print "\n";
  }
}
