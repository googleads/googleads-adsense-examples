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
import com.google.api.services.adsense.v2.model.AdClient;
import com.google.api.services.adsense.v2.model.ListAdClientsResponse;
import java.util.ArrayList;
import java.util.List;

/**
*
* This example gets all ad clients for the logged in user's default account.
*
* Tags: accounts.adclients.list
*
*/
public class GetAllAdClients {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @throws Exception
   */
  public static List<AdClient> run(Adsense adsense, String accountId, int maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all ad clients for account %s\n", accountId);
    System.out.println("=================================================================");

    // Retrieve ad client list in pages and display data as we receive it.
    List<AdClient> allAdClients = new ArrayList<AdClient>();
    String pageToken = null;
    do {
      ListAdClientsResponse response = adsense.accounts().adclients().list(accountId)
          .setPageSize(maxPageSize)
          .setPageToken(pageToken)
          .execute();
      List<AdClient> adClients = response.getAdClients();

      if (adClients != null && !adClients.isEmpty()) {
        allAdClients.addAll(adClients);
        for (AdClient adClient : adClients) {
          System.out.printf("Ad client for product \"%s\" with ID \"%s\" was found.\n",
              adClient.getProductCode(), adClient.getName());
          boolean supportsReporting = true;
          if (adClient.getReportingDimensionId() == null
              || adClient.getReportingDimensionId().isEmpty()) {
            supportsReporting = false;
          }
          System.out.printf("\tSupports reporting: %s\n", supportsReporting ? "No" : "Yes");
        }
      } else {
        System.out.println("No ad clients found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    return allAdClients;
  }
}
