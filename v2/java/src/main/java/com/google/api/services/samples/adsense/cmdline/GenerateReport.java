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
import com.google.api.services.adsense.v2.Adsense.Accounts.Reports.Generate;
import com.google.api.services.adsense.v2.model.Cell;
import com.google.api.services.adsense.v2.model.Header;
import com.google.api.services.adsense.v2.model.ReportResult;
import com.google.api.services.adsense.v2.model.Row;
import java.util.Arrays;
import java.util.List;

/**
 * This example retrieves a report, using a filter for a specified ad client.
 *
 * Tags: accounts.reports.generate
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
  public static void run(Adsense adsense, String accountId, String adClientId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running report for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    Generate request = adsense.accounts().reports().generate(accountId);

    // Specify the startDate and endDate for the report.
    request.setDateRange("CUSTOM");
    request.setStartDateYear(2021).setStartDateMonth(3).setStartDateDay(1);
    request.setEndDateYear(2021).setEndDateMonth(3).setEndDateDay(31);

    // Specify the desired ad client using a filter.
    request.setFilters(Arrays.asList("AD_CLIENT_ID==" + escapeFilterParameter(adClientId)));

    // Specify the desired metrics and dimensions.
    request.setMetrics(Arrays.asList("PAGE_VIEWS", "AD_REQUESTS", "AD_REQUESTS_COVERAGE", "CLICKS",
        "AD_REQUESTS_CTR", "COST_PER_CLICK", "AD_REQUESTS_RPM", "ESTIMATED_EARNINGS"));
    request.setDimensions(Arrays.asList("DATE"));

    // Sort by ascending date.
    request.setOrderBy(Arrays.asList("+DATE"));

    // Run report.
    ReportResult response = request.execute();
    List<Row> rows = response.getRows();

    if (rows != null && !rows.isEmpty()) {
      // Display headers.
      for (Header header : response.getHeaders()) {
        System.out.printf("%25s", header.getName());
      }
      System.out.println();

      // Display results.
      for (Row row : rows) {
        for (Cell cell : row.getCells()) {
          System.out.printf("%25s", cell.getValue());
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
    // Get the last entry of the parameter name (after the last forward slash) for filtering.
    parameter = parameter.substring(parameter.lastIndexOf("/") + 1);
    // Replace special characters with the escaped version and return the string.
    return parameter.replace("\\", "\\\\").replace(",", "\\,");
  }
}
