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
package com.google.adsensequickstart.api;

import com.google.adsensequickstart.AppStatus;

import java.io.IOException;
import java.util.List;


/**
 * Asynchronous task that fetches a report.
 */
public class AsyncFetchReport extends CommonAsyncTask {

  private final String fromDate;
  private final String toDate;
  private final List<String> dimensions;
  private final List<String> metrics;
  private final String accountId;

  private AsyncFetchReport(AsyncTaskController activity, ApiController apiController,
      String accountId, String fromDate, String toDate, List<String> dimensions,
      List<String> metrics) {
    super(activity, apiController);
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.dimensions = dimensions;
    this.metrics = metrics;
    this.accountId = accountId;
  }

  @Override
  protected void doInBackground() throws IOException {
    apiController.onReportFetched(
        apiController.getAdsenseService().accounts().reports()
          .generate(accountId, fromDate, toDate)
          .setDimension(dimensions)
          .setMetric(metrics)
          .execute());
  }

  @Override
  protected AppStatus getPostStatus() {
    return AppStatus.SHOWING_REPORT;
  }

  public static void run(AsyncTaskController activity, ApiController apiController,
      String accountId, String fromDate, String toDate, List<String> dimensions,
      List<String> metrics) {
    AsyncFetchReport task = new AsyncFetchReport(activity, apiController, accountId, fromDate,
        toDate, dimensions, metrics);
    activity.setActiveTask(task);
    task.execute();
  }
}