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
 * Possible unique states of the application.
 */
public enum AppStatus {

  NONE,
  GETTING_ACCOUNT_ID,
  FETCHING_INVENTORY,
  SHOWING_INVENTORY,
  FETCHING_METADATA,
  SHOWING_CUSTOM_CONFIG,
  FETCHING_REPORT,
  SHOWING_REPORT,
  FETCHING_SIMPLE_REPORT,
  PICKING_ACCOUNT
}