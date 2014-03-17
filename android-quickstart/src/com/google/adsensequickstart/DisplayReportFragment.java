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

import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse.Headers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


/**
 * Shows a report.
 */
public class DisplayReportFragment extends Fragment {

  private UiController displayReportController;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    ScrollView sv = new ScrollView(getActivity());
    TableLayout tl = new TableLayout(getActivity());
    sv.addView(tl);

    if (displayReportController == null) {
      return sv;
    }
    AdsenseReportsGenerateResponse response = displayReportController.getReportResponse();

    TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    tableRowParams.setMargins(10, 10, 10, 10);

    TableRow.LayoutParams tvParams = new TableRow.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    tvParams.setMargins(10, 10, 10, 10);

    List<Headers> headers = response.getHeaders();
    TableRow tr = new TableRow(getActivity());
    tl.addView(tr);

    for (Headers header : headers) {
      TextView tv = new TextView(getActivity());
      tv.setText(header.getName());
      tr.setLayoutParams(tableRowParams);
      tr.addView(tv);
    }
    if (response.getRows() != null && !response.getRows().isEmpty()) {
      for (List<String> row : response.getRows()) {
        TableRow trow = new TableRow(getActivity());
        tl.addView(trow);
        for (String cell : row) {
          TextView tv = new TextView(getActivity());
          tv.setText(cell);
          trow.addView(tv);
          tv.setLayoutParams(tvParams);
          tv.setPadding(15, 5, 15, 5);
          tv.setBackgroundColor(Color.WHITE);
        }
      }
    }
    return sv;
  }

  /**
   * Sets the controller to call back when a date is set.
   * @param displayReportController the callback controller
   */
  public void setUIController(UiController displayReportController) {
    this.displayReportController = displayReportController;
  }
}
