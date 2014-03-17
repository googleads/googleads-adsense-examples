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
import com.google.adsensequickstart.inventory.Inventory;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.AdClient;
import com.google.api.services.adsense.model.AdUnit;
import com.google.api.services.adsense.model.CustomChannel;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Asynchronous task that fetches an account's inventory.
 */
public class AsyncLoadInventory extends CommonAsyncTask {

  private final String rootAccountId;
  private Inventory inventory;

  private AsyncLoadInventory(AsyncTaskController activity, ApiController apiController,
      String accountId) {
    super(activity, apiController);
    rootAccountId = accountId;
  }

  @Override
  protected void doInBackground() throws IOException {
    inventory = new Inventory();
    List<String> accountIds = new ArrayList<String>();
    accountIds.add(rootAccountId);
    processAccount(rootAccountId);

    //Sub accounts:
    List<Account> subAccounts = apiController.getAdsenseService().accounts()
        .get(rootAccountId).setTree(true).execute().getSubAccounts();
    if (subAccounts != null) {
      for (Account account : subAccounts) {
        if (!accountIds.contains(account.getId())) {
          accountIds.add(account.getId());
          processAccount(account.getId());
        }
      }
    }
    inventory.setAccounts(accountIds);
    apiController.onInventoryFetched(inventory);
  }

  @Override
  protected AppStatus getPostStatus() {
    return AppStatus.SHOWING_INVENTORY;
  }

  public static void run(AsyncTaskController activity, ApiController apiController,
      String accountId) {
    AsyncLoadInventory task = new AsyncLoadInventory(activity, apiController, accountId);
    activity.setActiveTask(task);
    task.execute();
  }

  private void processAccount(String accountId) throws IOException {
    // Exit early if the AsyncTask is cancelled.
    if (isCancelled()) {
      return;
    }

    List<AdClient> adClients = apiController.getAdsenseService().accounts().adclients()
        .list(accountId).setMaxResults(10).execute().getItems();

    List<String> adClientIds = new ArrayList<String>();
    for (AdClient adClient : adClients) {
      adClientIds.add(adClient.getId());
      Log.d("LoadInventory", "AdClient: " + adClient.getId());
      List<AdUnit> adUnits = apiController.getAdsenseService().accounts().adunits()
          .list(accountId, adClient.getId()).setMaxResults(10).execute().getItems();
      List<String> adUnitNames = new ArrayList<String>();
      if (adUnits != null) {
        for (AdUnit adUnit : adUnits) {
          adUnitNames.add(adUnit.getName());
        }
      }
      inventory.setAdUnits(adClient.getId(), adUnitNames);

      List<CustomChannel> customChannels = apiController.getAdsenseService().accounts()
          .customchannels().list(accountId, adClient.getId()).setMaxResults(10).execute()
          .getItems();
      List<String> customChannelNames = new ArrayList<String>();
      if (customChannels != null) {
        for (CustomChannel customChannel : customChannels) {
          customChannelNames.add(customChannel.getName());
        }
      }
      inventory.setCustomChannels(adClient.getId(), customChannelNames);
    }
    inventory.setAdClients(accountId, adClientIds);
  }
}
