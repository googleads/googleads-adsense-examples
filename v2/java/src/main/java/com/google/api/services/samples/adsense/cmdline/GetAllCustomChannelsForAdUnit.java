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
import com.google.api.services.adsense.v2.model.CustomChannel;
import java.util.List;

/**
*
* This example gets all custom channels an ad unit has been added to.
*
* Tags: accounts.adclients.adunits.listLinkedCustomChannels
*
*/
public class GetAllCustomChannelsForAdUnit {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param adUnitId the ID for the ad unit to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @throws Exception
   */
  public static void run(Adsense adsense, String adUnitId, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all custom channels for ad unit %s\n", adUnitId);
    System.out.println("=================================================================");

    // Retrieve custom channel list in pages and display the data as we receive it.
    String pageToken = null;
    do {
      ListLinkedCustomChannelsResponse response =
          adsense.accounts().adclients().adunits().listLinkedCustomChannels(adUnitId)
              .setPageSize(maxPageSize)
              .setPageToken(pageToken)
              .execute();
      List<CustomChannel> customChannels = response.getCustomChannels();

      if (customChannels != null && !customChannels.isEmpty()) {
        for (CustomChannel channel : customChannels) {
          System.out.printf("Custom channel with code \"%s\" and name \"%s\" was found.\n",
              channel.getName(), channel.getDisplayName());
        }
      } else {
        System.out.println("No custom channels found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();
  }
}
