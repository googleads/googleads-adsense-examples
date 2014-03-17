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


/**
 * Asynchronous task that fetches the metadata for an account.
 */
public class AsyncLoadMetadata extends CommonAsyncTask {

  private AsyncLoadMetadata(AsyncTaskController activity, ApiController apiController) {
    super(activity, apiController);
  }

  @Override
  protected void doInBackground() throws IOException {
    apiController.onDimensionsFetched(
        apiController.getAdsenseService().metadata().dimensions().list().execute().getItems());
    apiController.onMetricsFetched(
        apiController.getAdsenseService().metadata().metrics().list().execute().getItems());
  }

  @Override
  protected AppStatus getPostStatus() {
    return AppStatus.SHOWING_CUSTOM_CONFIG;
  }

  public static void run(AsyncTaskController activity, ApiController apiController) {
    AsyncLoadMetadata task = new AsyncLoadMetadata(activity, apiController);
    activity.setActiveTask(task);
    task.execute();
  }
}