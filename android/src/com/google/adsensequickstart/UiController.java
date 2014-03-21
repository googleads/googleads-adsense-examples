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
package com.google.adsensequickstart;

import com.google.adsensequickstart.inventory.Inventory;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

import android.view.View;

import java.util.List;


/**
 * Defines the communication between fragments and the activity.
 */
public interface UiController {

  /**
   * Sends the selected dimensions and metrics to the controller to start a new report request.
   * @param dimensions the list of dimensions
   * @param metrics the list of metrics
   */
  public void loadReport(List<String> dimensions, List<String> metrics);

  /**
   * Used when a date has been selected.
   * @param year
   * @param month
   * @param day
   * @param isFrom true if the date is the start date, false otherwise
   */
  public void setDate(int year, int month, int day, boolean isFrom);

  /**
   * Gets the response that was fetched from the API.
   */
  public AdsenseReportsGenerateResponse getReportResponse();

  /**
   * Triggered when the "from" button or the "to" button is pressed.
   * @param button the button view
   */
  public void onDateBtClicked(View button);

  /**
   * Returns a list of all dimensions.
   */
  public List<ReportingMetadataEntry> getDimensions();

  /**
   * Returns a list of all metrics.
   */
  public List<ReportingMetadataEntry> getMetrics();

  /**
   * Returns the fetched inventory.
   */
  public Inventory getInventory();
}
