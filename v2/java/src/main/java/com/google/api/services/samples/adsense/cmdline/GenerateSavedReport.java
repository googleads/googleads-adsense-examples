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
import com.google.api.services.adsense.v2.Adsense.Accounts.Reports.Saved.Generate;
import com.google.api.services.adsense.v2.model.Cell;
import com.google.api.services.adsense.v2.model.Header;
import com.google.api.services.adsense.v2.model.ReportResult;
import com.google.api.services.adsense.v2.model.Row;
import java.util.List;

/**
 * This example retrieves a saved report for the default account.
 *
 * Tags: accounts.reports.saved.generate
 *
 */
public class GenerateSavedReport {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param savedReportId the saved report ID on which to run the report.
   * @throws Exception
   */
  public static void run(Adsense adsense, String savedReportId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running saved report %s\n", savedReportId);
    System.out.println("=================================================================");

    // Prepare report.
    Generate request = adsense.accounts().reports().saved().generate(savedReportId);
    request.setDateRange("LAST_7_DAYS");

    // Run saved report.
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
}
