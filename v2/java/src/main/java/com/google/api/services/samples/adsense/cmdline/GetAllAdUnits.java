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
import com.google.api.services.adsense.v2.model.AdUnit;
import com.google.api.services.adsense.v2.model.ListAdUnitsResponse;
import java.util.ArrayList;
import java.util.List;

/**
*
* This example gets all ad units in an ad client.
*
* Tags: accounts.adclients.adunits.list
*
*/
public class GetAllAdUnits {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the retrieved ad units.
   * @throws Exception
   */
  public static List<AdUnit> run(Adsense adsense, String adClientId, int maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all ad units for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve ad unit list in pages and display data as we receive it.
    String pageToken = null;
    List<AdUnit> allAdUnits = new ArrayList<AdUnit>();
    do {
      ListAdUnitsResponse response = adsense.accounts().adclients().adunits()
          .list(adClientId)
          .setPageSize(maxPageSize)
          .setPageToken(pageToken)
          .execute();
      List<AdUnit> adUnits = response.getAdUnits();

      if (adUnits != null && !adUnits.isEmpty()) {
        allAdUnits.addAll(adUnits);
        for (AdUnit unit : adUnits) {
          System.out.printf("Ad unit with id \"%s\", name \"%s\" and status \"%s\" was found.\n",
              unit.getName(), unit.getDisplayName(), unit.getState());
        }
      } else {
        System.out.println("No ad units found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    // Return the last page of ad units, so that the main sample has something to run.
    return allAdUnits;
  }
}
