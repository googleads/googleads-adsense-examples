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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * Shows a date picker and sends the result to the activity.
 */
public class DatePickerFragment extends DialogFragment implements
    DatePickerDialog.OnDateSetListener {
  private UiController callback;
  private boolean isFrom; // Is it "from" or "to"?

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Is it "from" or "to" date?.
    Bundle args = getArguments();
    isFrom = args.getBoolean("isFrom");
  
    // Use the current date as the default date in the picker.
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
  
    // Create a new instance of DatePickerDialog and return it.
    return new DatePickerDialog(getActivity(), this, year, month, day);
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user.
    if (callback != null) {
      callback.setDate(year, month, day, isFrom);
    }
  }

  /**
   * Sets the controller to call back when a date is set.
   * @param callback the callback controller
   */
  public void setCallback(UiController callback) {
    this.callback = callback;
  }
}
