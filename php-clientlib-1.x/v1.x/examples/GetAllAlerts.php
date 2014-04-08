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
 * Gets all alerts available for the specified account.
 *
 * Tags: accounts.alerts.list
 */
class GetAllAlerts {
  /**
   * Gets all alerts available for the specified account.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   */
  public static function run($service, $accountId) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all alerts for account %s\n", $accountId);
    print $separator;

    $alerts = null;
    $result = $service->accounts_alerts->listAccountsAlerts($accountId);
    if (!empty($result['items'])) {
      $alerts = $result['items'];
      foreach ($alerts as $alert) {
        $format = "Alert id \"%s\" with severity \"%s\" and type \"%s\" " .
            "was found.\n";
        printf($format, $alert['id'], $alert['severity'], $alert['type']);
      }
    } else {
      print "No alerts found.\n";
    }
    print "\n";
  }
}
