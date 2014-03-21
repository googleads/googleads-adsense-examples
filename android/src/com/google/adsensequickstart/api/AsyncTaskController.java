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
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.os.AsyncTask;


/**
 * Defines the communication with the task that makes requests to the API.
 */
public interface AsyncTaskController {
  /**
   * Shows or hides the progress bar.
   * @param task the reference to the task requesting the change
   * @param visible true to show the progress bar
   */
  void showProgressBar(AsyncTask<Void, Void, Boolean> task, boolean visible);

  /**
   * Shows error dialog if Google Play Services is not present.
   * @param connectionStatusCode the status of the connection
   */
  void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode);

  /**
   * Handle a user-recoverable error
   * @param userRecoverableException the raised exception
   */
  void handleRecoverableError(UserRecoverableAuthIOException userRecoverableException);

  /**
   * Set a new status to the app.
   * @param postStatus the new status
   */
  void setStatus(AppStatus postStatus);

  /**
   * Set the active task so that the app knows when to ignore signals from
   * cancelled tasks.
   * @param task the {@link AsyncTask} active from now on
   */
  void setActiveTask(AsyncTask<Void, Void, Boolean> task);
}