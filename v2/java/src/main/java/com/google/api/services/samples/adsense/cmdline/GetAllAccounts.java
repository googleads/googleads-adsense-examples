/*
 * Copyright (c) 2021 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.adsense.cmdline;

import com.google.api.services.adsense.v2.Adsense;
import com.google.api.services.adsense.v2.model.Account;
import com.google.api.services.adsense.v2.model.ListAccountsResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This example gets all accounts for the logged in user.
 *
 * Tags: accounts.list
 *
 */
public class GetAllAccounts {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the retrieved accounts.
   * @throws Exception
   */
  public static List<Account> run(Adsense adsense, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all AdSense accounts");
    System.out.println("=================================================================");

    List<Account> allAccounts = new ArrayList<Account>();
    // Retrieve account list in pages and display data as we receive it.
    String pageToken = null;
    do {
      ListAccountsResponse response = null;
      response = adsense.accounts().list()
          .setPageSize(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      List<Account> accounts = response.getAccounts();

      if (accounts != null && !accounts.isEmpty()) {
        allAccounts.addAll(accounts);
        for (Account account : accounts) {
          System.out.printf("Account with ID \"%s\" and name \"%s\" was found.\n",
              account.getName(), account.getDisplayName());
        }
      } else {
        System.out.println("No accounts found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    return allAccounts;
  }
}
