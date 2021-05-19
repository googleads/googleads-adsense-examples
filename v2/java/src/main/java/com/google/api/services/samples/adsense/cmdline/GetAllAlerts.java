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
import com.google.api.services.adsense.v2.model.Alert;
import com.google.api.services.adsense.v2.model.ListAlertsResponse;
import java.util.List;

/**
*
* Gets all alerts available for the logged in user's account.
*
* Tags: accounts.alerts.list
*
*/
public class GetAllAlerts {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @throws Exception
   */
  public static void run(Adsense adsense, String accountId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all alerts for account %s\n", accountId);
    System.out.println("=================================================================");

    // Retrieve and display alerts.
    ListAlertsResponse response = adsense.accounts().alerts().list(accountId).execute();
    List<Alert> alerts = response.getAlerts();

    if (alerts != null && !alerts.isEmpty()) {
      for (Alert alert : alerts) {
        System.out.printf("Alert id \"%s\" with severity \"%s\" and type \"%s\" was found.\n",
            alert.getName(), alert.getSeverity(), alert.getType());
      }
    } else {
      System.out.println("No alerts found.");
    }

    System.out.println();
  }
}
