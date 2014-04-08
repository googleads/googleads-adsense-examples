/*
 * Copyright (c) 2011 Google Inc.
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

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSense.Accounts.Reports.Generate;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;

import java.util.Arrays;
import java.util.List;

/**
 * This example retrieves a report, using a filter for a specified ad client.
 *
 * Tags: accounts.reports.generate
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class GenerateReport {
  /**
   * Runs this sample.
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @param adClientId the ad client ID on which to run the report.
   * @throws Exception
   */
  public static void run(AdSense adsense, String accountId, String adClientId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running report for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    String startDate = "today-7d";
    String endDate = "today-1d";
    Generate request = adsense.accounts().reports().generate(accountId, startDate, endDate);

    // Specify the desired ad client using a filter.
    request.setFilter(Arrays.asList("AD_CLIENT_ID==" + escapeFilterParameter(adClientId)));

    request.setMetric(Arrays.asList("PAGE_VIEWS", "AD_REQUESTS", "AD_REQUESTS_COVERAGE", "CLICKS",
        "AD_REQUESTS_CTR", "COST_PER_CLICK", "AD_REQUESTS_RPM", "EARNINGS"));
    request.setDimension(Arrays.asList("DATE"));

    // Sort by ascending date.
    request.setSort(Arrays.asList("+DATE"));

    // Run report.
    AdsenseReportsGenerateResponse response = request.execute();
    List<List<String>> rows = AdSenseSample.fillMissingDates(response);

    if (rows != null && !rows.isEmpty()) {
      // Display headers.
      for (AdsenseReportsGenerateResponse.Headers header : response.getHeaders()) {
        System.out.printf("%25s", header.getName());
      }
      System.out.println();

      // Display results.
      for (List<String> row : rows) {
        for (String column : row) {
          System.out.printf("%25s", column);
        }
        System.out.println();
        }

      System.out.println();
    } else {
      System.out.println("No rows returned.");
    }

    System.out.println();
  }

  /**
   * Escape special characters for a parameter being used in a filter.
   * @param parameter the parameter to be escaped.
   * @return the escaped parameter.
   */
  public static String escapeFilterParameter(String parameter) {
    return parameter.replace("\\", "\\\\").replace(",", "\\,");
  }
}
