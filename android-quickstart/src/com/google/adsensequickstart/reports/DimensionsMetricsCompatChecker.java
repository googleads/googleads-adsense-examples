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
package com.google.adsensequickstart.reports;

import com.google.api.services.adsense.model.ReportingMetadataEntry;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Utility class to check if sets of dimensions and metrics are compatible.
 *
 * <p>These functions use the metadata calls to retrieve the compatibility</p>
 */
public class DimensionsMetricsCompatChecker implements Serializable {

  private final List<ReportingMetadataEntry> mAllMetrics;
  private final List<ReportingMetadataEntry> mAllDimensions;

  public DimensionsMetricsCompatChecker(List<ReportingMetadataEntry> metrics,
      List<ReportingMetadataEntry> dimensions) {
    mAllMetrics = metrics;
    mAllDimensions = dimensions;
  }

  public static boolean isDimensionCompatibleWithMetrics(
      ReportingMetadataEntry dimension, List<String> metrics) {
    return dimension.getCompatibleMetrics().containsAll(metrics);
  }

  public static boolean isMetricCompatibleWithDimensions(ReportingMetadataEntry metric,
      List<String> dimensions) {
    // A metric is compatible if it's compatible with every selected dimension.
    return metric.getCompatibleDimensions().containsAll(dimensions);
  }

  public boolean areMetricsAndDimensionsCompatible(List<String> metrics,
      List<String> dimensions) {
    return (areDimensionsCompatible(dimensions) && areMetricsCompatible(metrics));
  }

  public boolean isMetricCompatibleWithDimensions(String metric,
      List<String> dimensions) {
    ReportingMetadataEntry entryMetric = getMetadataEntryMetric(metric);
    if (entryMetric == null) {
      return false;
    }
    return isMetricCompatibleWithDimensions(entryMetric, dimensions);
  }

  public boolean areMetricsCompatible(List<String> metrics) {
    for (String metricName : metrics) {
      ReportingMetadataEntry metric = getMetadataEntryMetric(metricName);
      if (!metric.getCompatibleMetrics().containsAll(metrics)) {
        return false;
      }
    }
    return true;
  }

  public boolean areDimensionsCompatible(List<String> dimensions) {
    return areDimensionsCompatible(null, dimensions);
  }

  public boolean isDimensionCompatibleWithDimensions(
      ReportingMetadataEntry dimension, List<String> dimensions) {
    return areDimensionsCompatible(dimension, dimensions);
  }

  public boolean isDimensionCompatibleWithDimensions(String dimensionId, List<String> dimensions) {
    ReportingMetadataEntry entryDimension = getMetadataEntryDimension(dimensionId);
    if (entryDimension == null) {
      return false;
    }
    return isDimensionCompatibleWithDimensions(entryDimension, dimensions);
  }

  public boolean isDimensionCompatibleWithMetrics(String dimensionId, List<String> metrics) {
    return isDimensionCompatibleWithMetrics(getMetadataEntryDimension(dimensionId), metrics);
  }

  public ReportingMetadataEntry getMetadataEntryDimension(String dimensionId) {
    for (ReportingMetadataEntry dimension : mAllDimensions) {
      if (dimension.getId().equals(dimensionId)) {
        return dimension;
      }
    }
    return null;
  }

  public ReportingMetadataEntry getMetadataEntryMetric(String metricId)  {
    for (ReportingMetadataEntry metric : mAllMetrics) {
      if (metric.getId().equals(metricId)) {
        return metric;
      }
    }
    return null;
  }

  private static void keepIntersection(List<String> originalList, List<String> otherList) {
    Iterator<String> iter = originalList.iterator();
    while (iter.hasNext()) {
      if (!otherList.contains(iter.next())) {
        iter.remove();
      }
    }
  }

  /*
   * A group of dimensions are compatible if there is a dimension compatibility
   * group that every dimension belongs to. This method checks for compatibility
   * between dimensions and an optional auxiliary dimension.
   */
  private boolean areDimensionsCompatible(@Nullable ReportingMetadataEntry dimension,
      List<String> dimensions) {

    List<String> compatibilityGroups;

    if (dimension == null) {
      compatibilityGroups = getMetadataEntryDimension(dimensions.get(0)).getCompatibleDimensions();
    } else {
      compatibilityGroups = dimension.getCompatibleDimensions();
    }

    for (String dimensionName : dimensions) {
      ReportingMetadataEntry otherDimension = getMetadataEntryDimension(dimensionName);
      keepIntersection(compatibilityGroups, otherDimension.getCompatibleDimensions());
    }
    return !compatibilityGroups.isEmpty();
  }
}
