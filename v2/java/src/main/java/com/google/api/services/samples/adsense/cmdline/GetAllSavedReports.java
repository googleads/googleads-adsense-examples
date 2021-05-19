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
import com.google.api.services.adsense.v2.model.ListSavedReportsResponse;
import com.google.api.services.adsense.v2.model.SavedReport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This example gets all saved reports for an account.
 *
 * Tags: accounts.reports.saved.list
 *
 */
public class GetAllSavedReports {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the retrieved saved reports.
   * @throws Exception
   */
  public static List<SavedReport> run(Adsense adsense, String accountId, int maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all saved reports for account %s\n", accountId);
    System.out.println("=================================================================");

    // Retrieve saved report list in pages and display the data as we receive it.
    String pageToken = null;
    List<SavedReport> allSavedReports = new ArrayList<SavedReport>();
    do {
      ListSavedReportsResponse response = adsense.accounts().reports().saved().list(accountId)
          .setPageSize(maxPageSize)
          .setPageToken(pageToken)
          .execute();
      List<SavedReport> savedReports = response.getSavedReports();

      if (savedReports != null && !savedReports.isEmpty()) {
        allSavedReports.addAll(savedReports);
        for (SavedReport savedReport : savedReports) {
          System.out.printf("Saved report with id \"%s\" and name \"%s\" was found.\n",
              savedReport.getName(), savedReport.getTitle());
        }
      } else {
        System.out.println("No saved reports found.");
      }

      pageToken = response.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    return allSavedReports;
  }
}
