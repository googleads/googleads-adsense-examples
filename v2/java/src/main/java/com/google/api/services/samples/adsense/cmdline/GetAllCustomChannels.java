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
import com.google.api.services.adsense.v2.model.ListCustomChannelsResponse;
import java.util.ArrayList;
import java.util.List;

/**
*
* This example gets all custom channels in an ad client.
*
* Tags: accounts.adclients.customchannels.list
*
*/
public class GetAllCustomChannels {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the retrieved custom channels.
   * @throws Exception
   */
  public static List<CustomChannel> run(Adsense adsense, String adClientId,
      int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all custom channels for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve custom channel list in pages and display the data as we receive it.
    String pageToken = null;
    List<CustomChannel> allCustomChannels = new ArrayList<CustomChannel>();
    do {
      ListCustomChannelsResponse response = adsense.accounts().adclients().customchannels()
          .list(adClientId)
          .setPageSize(maxPageSize)
          .setPageToken(pageToken)
          .execute();
      List<CustomChannel> customChannels = response.getCustomChannels();

      if (customChannels != null && !customChannels.isEmpty()) {
        allCustomChannels.addAll(customChannels);
        for (CustomChannel channel : customChannels) {
          System.out.printf("Custom channel with id \"%s\" and name \"%s\" was found.\n",
              channel.getName(), channel.getDisplayName());
        }
      } else {
        System.out.println("No custom channels found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();
    return allCustomChannels;
  }
}
