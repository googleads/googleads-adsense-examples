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

import com.google.adsensequickstart.reports.DimensionsMetricsCompatChecker;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that lets the user choose dimensions, metrics and dates to generate
 * a custom report.
 */
public class CustomReportConfigFragment extends Fragment implements DimensionMetricChangeListener,
    OnClickListener {

  private DimensionMetricAdapter dimensionAdapter, metricAdapter;
  private ArrayList<UiReportingItem> dimensionsUI;
  private ArrayList<UiReportingItem> metricsUI;
  private UiController customReportConfigReadyController;
  private DimensionsMetricsCompatChecker checker;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.custom_report_config, container, false);

    TabHost host = (TabHost) rootView.findViewById(R.id.tabhost);
    host.setup();

    TabSpec spec = host.newTabSpec("Dimensions");
    spec.setContent(R.id.dimensions_tab);
    spec.setIndicator("Dimensions");
    host.addTab(spec);

    spec = host.newTabSpec("Metrics");
    spec.setContent(R.id.metrics_tab);
    spec.setIndicator("Metrics");
    host.addTab(spec);

    spec = host.newTabSpec("Dates");
    spec.setContent(R.id.dates_tab);
    spec.setIndicator("Dates");
    host.addTab(spec);

    if (customReportConfigReadyController == null) {
      return rootView;
    }
    List<ReportingMetadataEntry> dimensions = customReportConfigReadyController.getDimensions();
    List<ReportingMetadataEntry> metrics = customReportConfigReadyController.getMetrics();


    checker = new DimensionsMetricsCompatChecker(metrics, dimensions);

    // Get a list of dimension IDs
    ArrayList<String> dimensionIds = new ArrayList<String>();

    for (ReportingMetadataEntry dimension : dimensions) {
      dimensionIds.add(dimension.getId());
    }

    // Get a list of metric IDs
    ArrayList<String> metricIds = new ArrayList<String>();

    for (ReportingMetadataEntry metric : metrics) {
      metricIds.add(metric.getId());
    }

    dimensionsUI = new ArrayList<UiReportingItem>();
    for (String dimension : dimensionIds) {
      dimensionsUI.add(new UiReportingItem(dimension, false, true));
    }

    dimensionAdapter = new DimensionMetricAdapter(getActivity(), R.layout.custom_report_list_item,
        dimensionsUI, false);
    ListView lvdimension = (ListView) rootView.findViewById(R.id.dimensions_list);
    lvdimension.setAdapter(dimensionAdapter);

    metricsUI = new ArrayList<UiReportingItem>();
    for (String metric : metricIds) {
      metricsUI.add(new UiReportingItem(metric, false, true));
    }

    metricAdapter = new DimensionMetricAdapter(
        getActivity(), R.layout.custom_report_list_item, metricsUI, true);
    ListView lvmetric = (ListView) rootView.findViewById(R.id.metrics_list);
    lvmetric.setAdapter(metricAdapter);

    dimensionAdapter.setChangeListener(this);
    metricAdapter.setChangeListener(this);

    rootView.findViewById(R.id.generate_bt).setOnClickListener(this);
    rootView.findViewById(R.id.from_bt).setOnClickListener(this);
    rootView.findViewById(R.id.to_bt).setOnClickListener(this);

    return rootView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.generate_bt:
        if (customReportConfigReadyController != null) {
          customReportConfigReadyController.loadReport(
              computeCheckedItems(dimensionsUI), computeCheckedItems(metricsUI));
        }
        break;
      case R.id.from_bt:
        if (customReportConfigReadyController != null) {
          customReportConfigReadyController.onDateBtClicked(v);
        }
        break;
      case R.id.to_bt:
        if (customReportConfigReadyController != null) {
          customReportConfigReadyController.onDateBtClicked(v);
        }
        break;
      default:
        break;
    }
  }
  @Override
  public void onSelected(int position, boolean isMetric, boolean isChecked) {

    if (isMetric) {
      metricsUI.get(position).setChecked(isChecked);

      List<String> checkedMetrics = computeCheckedItems(metricsUI);

      for (UiReportingItem dimension : dimensionsUI) {
        boolean isCompatible = checker.isDimensionCompatibleWithMetrics(
            dimension.getId(), checkedMetrics);
        dimension.setEnabled(isCompatible);
      }
      dimensionAdapter.notifyDataSetChanged();
      metricAdapter.notifyDataSetChanged();
    } else { // is dimension

      dimensionsUI.get(position).setChecked(isChecked);
      List<String> checkedDimensions = computeCheckedItems(dimensionsUI);

      // Check the rest of dimensions and metrics
      for (UiReportingItem dimension : dimensionsUI) {
        boolean isCompatible = checker.isDimensionCompatibleWithDimensions(
            dimension.getId(), checkedDimensions);
        dimension.setEnabled(isCompatible);
      }

      for (UiReportingItem metric : metricsUI) {
        boolean isCompatible = checker.isMetricCompatibleWithDimensions(
            metric.getId(), checkedDimensions);
        metric.setEnabled(isCompatible);
      }

      dimensionAdapter.notifyDataSetChanged();
      metricAdapter.notifyDataSetChanged();
    }
  }

  /**
   * Sets the controller to call back when a date is set.
   * @param customReportConfigReadyController the callback controller
   */
  public void setUIController(UiController customReportConfigReadyController) {
    this.customReportConfigReadyController = customReportConfigReadyController;
  }

  private static List<String> computeCheckedItems(List<UiReportingItem> collection) {
    List<String> checkedItems = new ArrayList<String>();
    for (UiReportingItem item : collection) {
      if (item.isChecked()) {
        checkedItems.add(item.getId());
      }
    }
    return checkedItems;
  }
}

