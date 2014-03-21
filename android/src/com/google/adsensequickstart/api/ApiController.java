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

import com.google.adsensequickstart.inventory.Inventory;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

/**
 * Defines the communication needed for the task that fetches reports.
 */
public class ApiController {

  private static final String PREF_ACCOUNT_NAME = "accountName";
  private static ApiController apiController;
  private final AdSense adsenseService;
  private final GoogleAccountCredential credential;
  private final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
  private final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private Activity activity;
  private List<ReportingMetadataEntry> dimensions;
  private List<ReportingMetadataEntry> metrics;
  private Inventory inventory;
  private List<Account> accounts;
  private AdsenseReportsGenerateResponse reportResponse;

  public ApiController(Activity activity) {

    this.activity = activity;

    // API credentials
    credential = GoogleAccountCredential.usingOAuth2(
        activity.getApplicationContext(), Collections.singleton(AdSenseScopes.ADSENSE));
    SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

    // Set up AdSense Management API client.
    adsenseService = new AdSense.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("AdSense Quickstart for Android").build();

  }

  public static ApiController getApiController(Activity activity) {
    if (apiController == null) {
      apiController = new ApiController(activity);
    }
    apiController.activity = activity;
    return apiController;
  }

  /**
   * Triggered when a report has been fetched
   * @param response the response from the API
   */
  public void onReportFetched(AdsenseReportsGenerateResponse response) {
    reportResponse = response;
  }

  /**
   * Called when the accounts have been fetched.
   * @param accounts the list of accounts
   */
  public void onAccountsFetched(List<Account> accounts) {
    this.accounts = accounts;
  }

  /**
   * Called when the inventory has been fetched.
   * @param inventory the fetched {@link Inventory}
   */
  public void onInventoryFetched(Inventory inventory) {
    this.inventory = inventory;
  }

  /**
   * Called when the dimensions are fetched.
   * @param items the list of dimensions
   */
  public void onDimensionsFetched(List<ReportingMetadataEntry> items) {
     dimensions = items;
   }

  /**
   * Called when the metrics are fetched.
   * @param items the list of metrics
   */
  public void onMetricsFetched(List<ReportingMetadataEntry> items) {
    metrics = items;
  }

  public void reset() {
    metrics = null;
    dimensions = null;
    inventory = null;
    reportResponse = null;
  }

  public void setAccountName(String accountName) {
    credential.setSelectedAccountName(accountName);
    SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(PREF_ACCOUNT_NAME, accountName);
    editor.commit();
  }

  public void loadMetadata() {
    AsyncLoadMetadata.run((AsyncTaskController) activity, this);
  }

  public void loadAccounts() {
    AsyncLoadAccounts.run((AsyncTaskController) activity, this);
  }

  public void loadInventory(String publisherAccountId) {
    AsyncLoadInventory.run((AsyncTaskController) activity, this, publisherAccountId);
  }

  public void loadReport(String accountId, String fromDate, String toDate, List<String> dimensions,
      List<String> metrics) {
    AsyncFetchReport.run((AsyncTaskController) activity, this, accountId, fromDate, toDate,
        dimensions, metrics);
  }

  public List<ReportingMetadataEntry> getDimensions() {
    return dimensions;
  }

  public List<ReportingMetadataEntry> getMetrics() {
    return metrics;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public AdsenseReportsGenerateResponse getReportResponse() {
    return reportResponse;
  }

  public AdSense getAdsenseService() {
    return adsenseService;
  }

  public GoogleAccountCredential getCredential() {
    return credential;
  }

}
