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
import com.google.adsensequickstart.ErrorUtils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;


/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 */
abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {
  private final String tag = "CommonAsyncTask";
  protected final ApiController apiController;
  final AsyncTaskController activity;

  protected CommonAsyncTask(AsyncTaskController activity, ApiController apiController) {
    this.activity = activity;
    this.apiController = apiController;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    activity.showProgressBar(this, true);
  }

  @Override
  protected final void onPostExecute(Boolean success) {
    super.onPostExecute(success);
    activity.showProgressBar(this, false);
    if (success) {
      activity.setStatus(getPostStatus());
    }
  }

  @Override
  protected void onCancelled(Boolean result) {
    super.onCancelled(result);
    activity.showProgressBar(this, false);
  }

  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
      doInBackground();
      return true;
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
      activity.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
      activity.handleRecoverableError(userRecoverableException);
    } catch (IOException e) {
      ErrorUtils.logAndShow((Activity) activity, tag, e);
    }
    return false;
  }

  protected abstract void doInBackground() throws IOException;

  protected abstract AppStatus getPostStatus();
}