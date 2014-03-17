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
import com.google.api.services.adsense.model.Account;

import java.io.IOException;
import java.util.List;


/**
 * Asynchronous task that fetches the AdSense accounts.
 */
public class AsyncLoadAccounts extends CommonAsyncTask {

    private AsyncLoadAccounts(AsyncTaskController activity, ApiController apiController) {
      super(activity, apiController);
    }

    @Override
    protected void doInBackground() throws IOException {
      List<Account> items = apiController.getAdsenseService().accounts().list().execute()
          .getItems();
      apiController.onAccountsFetched(items);
    }

    @Override
    protected AppStatus getPostStatus() {
      return AppStatus.PICKING_ACCOUNT;
    }

    public static void run(AsyncTaskController activity, ApiController apiController) {
      AsyncLoadAccounts task = new AsyncLoadAccounts(activity, apiController);
      activity.setActiveTask(task);
      task.execute();
    }
  }