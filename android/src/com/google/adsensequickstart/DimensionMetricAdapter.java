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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;


/**
 * Adapter used to show metrics and dimensions in a list.
 */
public class DimensionMetricAdapter extends ArrayAdapter<UiReportingItem> {

  private final Context context;
  private final int resourceID;
  private final List<UiReportingItem> textItems;
  private final boolean isMetric;
  private DimensionMetricChangeListener changeListener;

  public DimensionMetricAdapter(Context context, int resourceID, List<UiReportingItem> textItems,
     boolean isMetric) {
    super(context, resourceID, textItems);

    this.context = context;
    this.resourceID = resourceID;
    this.textItems = textItems;
    this.isMetric = isMetric;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(resourceID, parent, false);
    CheckBox cb = (CheckBox) rowView.findViewById(R.id.custom_report_checkbox);
    cb.setText(textItems.get(position).getId());
    cb.setChecked(textItems.get(position).isChecked());
    cb.setTag(position);
    cb.setEnabled(textItems.get(position).isEnabled());
    cb.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View cb) {
        checkboxChanged((Integer) ((CheckBox) cb).getTag(), isMetric, ((CheckBox) cb).isChecked());
      }
    });
    return rowView;
  }

  public void setChangeListener(DimensionMetricChangeListener listener) {
    changeListener = listener;
  }

  private void checkboxChanged(int position, boolean isMetric, boolean isChecked) {
    if (changeListener != null) {
      changeListener.onSelected(position, isMetric, isChecked);
    }
  }
}
