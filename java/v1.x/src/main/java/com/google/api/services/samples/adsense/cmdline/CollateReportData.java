/*
 * Copyright (c) 2014 Google Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Retrieves two reports and collates data between them, taking into account any
 * missing "categories". For example, if a platform is missing from one of the
 * requests in this sample, metric values will be filled in automatically with
 * zeroes.
 *
 * Tags: reports.generate
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class CollateReportData {
  /**
   * Runs this sample.
   * @param adsense AdSense service object on which to run the requests.
   * @param accountId the ID for the account to be used.
   * @param adClientId the ad client ID on which to run the report.
   * @throws Exception
   */
  public static void run(AdSense adsense, String accountId, String adClientId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running reports for ad client %s, and collating data\n", adClientId);
    System.out.println("=================================================================");

    // The first report is for "last week", i.e., the 7-day period ending yesterday.
    Generate lastWeekRequest =
        adsense.accounts().reports().generate(accountId, "today-7d", "today-1d");
    // The second report is for the "previous week", i.e., the 7-day period ending eight days ago.
    Generate prevWeekRequest =
        adsense.accounts().reports().generate(accountId, "today-14d", "today-8d");

    lastWeekRequest.setMetric(Arrays.asList("CLICKS", "EARNINGS"));
    prevWeekRequest.setMetric(Arrays.asList("CLICKS", "EARNINGS"));
    lastWeekRequest.setDimension(Arrays.asList("PLATFORM_TYPE_CODE", "PLATFORM_TYPE_NAME"));
    prevWeekRequest.setDimension(Arrays.asList("PLATFORM_TYPE_CODE", "PLATFORM_TYPE_NAME"));

    // Sort by ascending PLATFORM_TYPE_CODE.
    lastWeekRequest.setSort(Arrays.asList("+PLATFORM_TYPE_CODE"));
    prevWeekRequest.setSort(Arrays.asList("+PLATFORM_TYPE_CODE"));

    // Run reports.
    AdsenseReportsGenerateResponse lastWeekResponse = lastWeekRequest.execute();
    AdsenseReportsGenerateResponse prevWeekResponse = prevWeekRequest.execute();
    AdsenseReportsGenerateResponse[] responses = { lastWeekResponse, prevWeekResponse };

    // Create new lists for filled data with the existing data in the responses.
    List<List<String>> lastRows = new ArrayList<List<String>>();
    List<List<String>> prevRows = new ArrayList<List<String>>();

    // Add existing data to new lists.
    if (lastWeekResponse.getRows() != null && !lastWeekResponse.getRows().isEmpty()) {
      lastRows.addAll(lastWeekResponse.getRows());
    }
    if (prevWeekResponse.getRows() != null && !prevWeekResponse.getRows().isEmpty()) {
      prevRows.addAll(prevWeekResponse.getRows());
    }

    // Compile complete set of platforms and platform codes across both report responses.
    Map<String, String> platformNames = new HashMap<String, String>();
    for (AdsenseReportsGenerateResponse response : responses) {
      if (response.getRows() != null && !response.getRows().isEmpty()) {
        for (List<String> row : response.getRows()) {
          platformNames.put(row.get(0), row.get(1));
        }
      }
    }
    Set<String> platforms = platformNames.keySet();


    // How many metrics have we got?
    int metrics = 0;
    if (lastRows.size() > 0) {
      // Subtracting 2 to skip headers for dimensions.
      metrics = lastRows.get(0).size() - 2;
    } else if (prevRows.size() > 0) {
      // Subtracting 2 to skip headers for dimensions.
      metrics = prevRows.get(0).size() - 2;
    }

    // Add missing data to both datasets.
    List<List<List<String>>> datasets = Arrays.asList(lastRows, prevRows);
    for (List<List<String>> dataset : datasets) {
      if (dataset.size() < platforms.size()) {
        // Compile list of platforms in this dataset.
        List<String> datasetPlatforms = new ArrayList<String>();
        for (List<String> row : dataset) {
          datasetPlatforms.add(row.get(0));
        }

        // Compile list of missing platforms in this dataset.
        List<String> missing = new ArrayList<String>(platforms);
        missing.removeAll(datasetPlatforms);

        // Add data for all missing platforms.
        for (String platform : missing) {
          List<String> newRow = new ArrayList<String>();
          // Add platform code and name.
          newRow.add(platform);
          newRow.add(platformNames.get(platform));
          // Add metrics.
          for (int i = 0; i < metrics; i++) {
            newRow.add("0");
          }
          dataset.add(newRow);
        }

        // Sort dataset.
        Collections.sort(dataset, new Comparator<List<String>>() {
          public int compare(List<String> a, List<String> b) {
            return a.get(0).compareTo(b.get(0));
          }
        });
      }
    }

    // Display effective date range.
    System.out.printf("Results for last week (%s to %s) versus the previous week (%s to %s).\n",
        lastWeekResponse.getStartDate(),
        lastWeekResponse.getEndDate(),
        prevWeekResponse.getStartDate(),
        prevWeekResponse.getEndDate());
    System.out.println();

    // Display collated data.
    for (int platformIndex = 0; platformIndex < platforms.size(); platformIndex++) {
      List<String> lastRow = lastRows.get(platformIndex);
      List<String> prevRow = prevRows.get(platformIndex);
      String platform = lastRows.get(platformIndex).get(0);
      System.out.printf("%s:\n", platformNames.get(platform));
      // Adding 2 to skip headers for dimensions.
      for (int metricIndex = 2; metricIndex < metrics + 2; metricIndex++) {
        String metricName = lastWeekResponse.getHeaders().get(metricIndex).getName();
        System.out.printf("- %f delta (%s last week vs %s in the previous week) on %s\n",
            Float.valueOf(lastRow.get(metricIndex)) - Float.valueOf(prevRow.get(metricIndex)),
            lastRow.get(metricIndex),
            prevRow.get(metricIndex),
            metricName);
      }
    }

    System.out.println();
  }
}
