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
 * Gets a specific account for the logged in user.
 * This includes the full tree of sub-accounts.
 *
 * Tags: accounts.get
 */
class GetAccountTree {
  /**
   * Gets a specific account for the logged in user.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   * @param $accountId string the ID for the account to be used.
   */
  public static function run($service, $accountId) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Displaying AdSense account tree for %s\n", $accountId);
    print $separator;

    $account = $service->accounts->get($accountId);
    self::displayTree($service, $account, 0);

    print "\n";
  }

  /**
   * Auxiliary method to recurse through the account tree, displaying it.
   */
  private static function displayTree($service, $parentAccount, $level) {
    print str_repeat(' ', $level);
    printf("Account with ID \"%s\" and name \"%s\" was found.\n",
        $parentAccount['name'], $parentAccount['displayName']);

    $childAccounts = $service->accounts->listChildAccounts($parentAccount['name']);
    if (!empty($childAccounts)) {
      foreach ($childAccounts as $childAccount) {
        self::displayTree($childAccount, $level + 1);
      }
    }
  }
}
