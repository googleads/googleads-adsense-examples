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
 * This example gets all custom channels in an ad client.
 *
 * Tags: accounts.customchannels.list
 */
class GetAllCustomChannels {
  /**
   * Gets all custom channels in an ad client.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $adClientId string the ID for the ad client to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   * @return array the last page of retrieved custom channels.
   */
  public static function run($service, $adClientId, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all custom channels for ad client %s\n", $adClientId);
    print $separator;

    $optParams['pageSize'] = $maxPageSize;

    $pageToken = null;
    $customChannels = null;
    do {
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts_adclients_customchannels
                        ->listAccountsAdclientsCustomchannels($adClientId,
                                                              $optParams);
      if (!empty($result['customChannels'])) {
        $customChannels = $result['customChannels'];
        foreach ($customChannels as $customChannel) {
          printf("Custom channel with ID \"%s\" and name \"%s\" was found.\n",
              $customChannel['name'], $customChannel['displayName']);
        }
        $pageToken = $result['nextPageToken'];
      } else {
        print "No custom channels found.\n";
      }
    } while ($pageToken);
    print "\n";

    return $customChannels;
  }
}
