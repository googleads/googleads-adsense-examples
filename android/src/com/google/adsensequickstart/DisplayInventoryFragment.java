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


/**
 * Fragment that displays the inventory tree.
 */
public class DisplayInventoryFragment extends Fragment {

  private UiController displayInventoryController;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    ScrollView sv = new ScrollView(getActivity());
    TableLayout tl = new TableLayout(getActivity());
    tl.setBackgroundColor(Color.rgb(242, 239, 233));
    sv.addView(tl);

    if (displayInventoryController == null) {
      return sv;
    }
    Inventory  inventory = displayInventoryController.getInventory();

    TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    tableRowParams.setMargins(1, 1, 1, 1);

    TableRow.LayoutParams accountLayoutParams = new TableRow.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    accountLayoutParams.setMargins(2, 1, 2, 1);

    TableRow.LayoutParams adCLientLayoutParams = new TableRow.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    adCLientLayoutParams.setMargins(12, 1, 2, 1);

    TableRow.LayoutParams adUnitChannelLayoutParams = new TableRow.LayoutParams(
        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    adUnitChannelLayoutParams.setMargins(24, 1, 2, 1);

    for (String accountId : inventory.getAccounts()) {
      TableRow trow = new TableRow(getActivity());
      tl.addView(trow);
      TextView tv = new TextView(getActivity());
      tv.setText(accountId);
      trow.addView(tv);
      tv.setLayoutParams(accountLayoutParams);

      for (String adClient : inventory.getAdClients(accountId)) {
        TableRow trow2 = new TableRow(getActivity());
        trow2.setBackgroundColor(Color.rgb(214, 204, 181));
        tl.addView(trow2);
        TextView tv2 = new TextView(getActivity());
        tv2.setText(adClient);
        trow2.addView(tv2);
        tv2.setLayoutParams(adCLientLayoutParams);
        for (String adUnit : inventory.getAdUnits(adClient)) {
          TableRow trow3 = new TableRow(getActivity());
          trow3.setBackgroundColor(Color.rgb(251, 145, 57));
          tl.addView(trow3);
          TextView tv3 = new TextView(getActivity());
          tv3.setText(adUnit);
          trow3.addView(tv3);
          tv3.setLayoutParams(adUnitChannelLayoutParams);
        }
        for (String customChannel : inventory.getCustomChannels(adClient)) {
          TableRow trow3 = new TableRow(getActivity());
          trow3.setBackgroundColor(Color.rgb(255, 195, 69));
          tl.addView(trow3);
          TextView tv3 = new TextView(getActivity());
          tv3.setText(customChannel);
          trow3.addView(tv3);
          tv3.setLayoutParams(adUnitChannelLayoutParams);
        }
      }
    }
    return sv;
  }

  /**
   * Sets the controller to call back when a date is set.
   * @param displayInventoryController the callback controller
   */
  public void setUIController(UiController displayInventoryController) {
    this.displayInventoryController = displayInventoryController;
  }

}
