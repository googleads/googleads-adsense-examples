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


/**
 * Listens to metric and dimension changes by the user.
 */
public interface DimensionMetricChangeListener {

  /**
   * The user taps a checkbox.
   * @param position the position of the metric/dimension
   * @param isMetric true if metric, false if dimension
   * @param isChecked true if checkbox is checked
   */
  public void onSelected(int position, boolean isMetric, boolean isChecked);
}