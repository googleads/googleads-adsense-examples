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
 * This example gets all ad units in an ad client.
 *
 * Tags: accounts.adunits.list
 */
class GetAllAdUnits {
  /**
   * Gets all ad units in an ad client.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $adClientId string the ID for the ad client to be used.
   * @param $maxPageSize int the maximum page size to retrieve.
   * @return array the last page of retrieved ad units.
   */
  public static function run($service, $adClientId, $maxPageSize) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all ad units for ad client %s\n", $adClientId);
    print $separator;

    $optParams['pageSize'] = $maxPageSize;

    $pageToken = null;
    $adUnits = null;
    do {
      printf("%s\n", $pageToken);
      $optParams['pageToken'] = $pageToken;
      $result = $service->accounts_adclients_adunits
                        ->listAccountsAdclientsAdunits($adClientId,$optParams);
      if (!empty($result['adUnits'])) {
        $adUnits = $result['adUnits'];
        foreach ($adUnits as $adUnit) {
          $format = "Ad unit with ID \"%s\", name \"%s\" and state \"%s\" " .
              "was found.\n";
          printf($format, $adUnit['name'], $adUnit['displayName'], $adUnit['state']);
        }
        $pageToken = $result['nextPageToken'];
      } else {
        print "No ad units found.\n";
      }
    } while ($pageToken);
    print "\n";

    return $adUnits;
  }
}
