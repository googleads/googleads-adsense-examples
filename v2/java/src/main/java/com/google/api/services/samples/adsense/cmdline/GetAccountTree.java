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
import com.google.api.services.adsense.v2.model.ListChildAccountsResponse;
import java.util.List;

/**
*
* This example gets a specific account for the logged in user.
* This includes the full tree of sub-accounts.
*
* Tags: accounts.get, accounts.listChildAccounts
*
*/
public class GetAccountTree {

  /**
   * Auxiliary method to recursively fetch child accounts, displaying them in a tree.
   * @param parentAccount the account to be print a sub-tree for.
   * @param level the depth at which the top account exists in the tree.
   */
  private static void displayTree(Adsense adsense, Account parentAccount, int level)
      throws Exception {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.printf("Account with ID \"%s\" and name \"%s\" was found.\n",
        parentAccount.getName(), parentAccount.getDisplayName());

    ListChildAccountsResponse response = adsense.accounts()
        .listChildAccounts(parentAccount.getName()).execute();
    List<Account> subAccounts = response.getAccounts();

    if (subAccounts != null && !subAccounts.isEmpty()) {
      for (Account subAccount : subAccounts) {
        displayTree(adsense, subAccount, level + 1);
      }
    }
  }

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @throws Exception
   */
  public static void run(Adsense adsense, String accountId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Displaying AdSense account tree for %s\n", accountId);
    System.out.println("=================================================================");

    // Retrieve account.
    Account account = adsense.accounts().get(accountId).execute();

    displayTree(adsense, account, 0);

    System.out.println();
  }
}
