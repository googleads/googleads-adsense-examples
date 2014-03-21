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

import com.google.adsensequickstart.api.ApiController;
import com.google.adsensequickstart.api.AsyncTaskController;
import com.google.adsensequickstart.inventory.Inventory;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Main activity of the AdSense Quickstart sample application.
 */
public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener,
    UiController, AsyncTaskController {

  private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
  private static final int REQUEST_AUTHORIZATION = 1;
  private static final int REQUEST_ACCOUNT_PICKER = 2;
  private static final String TAG = "AdSenseQuickstart";
  private static final String DATE_PICKER_TAG = "datePicker";
  private static FragmentManager fragmentManager;

  private String fromDate;
  private String toDate;
  private AppStatus status = AppStatus.GETTING_ACCOUNT_ID;
  private String publisherAccountId;
  private AsyncTask<Void, Void, Boolean> asyncTask;
  private ApiController apiController;

  /**
   * Tells the app that there's a new task to be run and cancels the previous.
   *
   * @param task the new task
   */
  @Override
  public void setActiveTask(AsyncTask<Void, Void, Boolean> task) {
    if (asyncTask != null) {
      asyncTask.cancel(true);
    }
    asyncTask = task;
  }

  @Override
  public void showProgressBar(AsyncTask<Void, Void, Boolean> task, boolean visible) {
    // Only modify the progress bar if the task is active.
    if (asyncTask == task) {
      setProgressBarIndeterminateVisibility(visible);
    }
  }

  @Override
  public void handleRecoverableError(UserRecoverableAuthIOException error) {
    startActivityForResult(error.getIntent(), REQUEST_AUTHORIZATION);
  }

  @Override
  public void onDateBtClicked(View view) {
    DatePickerFragment dpFragment = new DatePickerFragment();
    dpFragment.setCallback(this);
    Bundle args = new Bundle();
    args.putBoolean("isFrom", view.getId() == R.id.from_bt);
    dpFragment.setArguments(args);
    dpFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
  }

  @Override
  public List<ReportingMetadataEntry> getDimensions() {
    return apiController.getDimensions();
  }

  @Override
  public List<ReportingMetadataEntry> getMetrics() {
    return apiController.getMetrics();
  }

  @Override
  public Inventory getInventory() {
    return apiController.getInventory();
  }

  @Override
  public void loadReport(List<String> dimensions, List<String> metrics) {
    if (fromDate == null) {
      Toast.makeText(this, "Please choose a start date", Toast.LENGTH_SHORT).show();
      return;
    }
    if (toDate == null) {
      Toast.makeText(this, "Please choose an end date", Toast.LENGTH_SHORT).show();
      return;
    }
    if ((dimensions.isEmpty()) || metrics.isEmpty()) {
      Toast.makeText(this, "Please choose at least a dimension and a metric", Toast.LENGTH_SHORT)
          .show();
      return;
    }
    apiController.loadReport(publisherAccountId, fromDate, toDate, dimensions, metrics);
  }

  @Override
  public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode,
            MainActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }

  @Override
  public boolean onNavigationItemSelected(int position, long id) {
    Log.d(TAG, String.format("onNavigationItemSelected: %d", position));
    if ((apiController.getAccounts() == null) || (publisherAccountId == null)) {
      status = AppStatus.GETTING_ACCOUNT_ID;
      refreshView();
      return true;
    }
    switch (position) {
      case 0:
        if (status != AppStatus.FETCHING_INVENTORY) {
          status = AppStatus.FETCHING_INVENTORY;
          refreshView();
        }
        break;
      case 1:
        if (status != AppStatus.FETCHING_SIMPLE_REPORT) {
          status = AppStatus.FETCHING_SIMPLE_REPORT;
          refreshView();
        }
        break;
      case 2:
        if (status != AppStatus.FETCHING_METADATA) {
          status = AppStatus.FETCHING_METADATA;
          refreshView();
        }
        break;
      default:
        throw new UnsupportedOperationException("Not implemented");
    }
    return true;
  }

  @Override
  public AdsenseReportsGenerateResponse getReportResponse() {
    return apiController.getReportResponse();
  }

  @Override
  public void setDate(int year, int month, int day, boolean isFrom) {
    if (isFrom) {
      fromDate = String.format("%d-%02d-%02d", year, month + 1, day);
    } else {
      toDate = String.format("%d-%02d-%02d", year, month + 1, day);
    }
  }

  @Override
  public void setStatus(AppStatus status) {
    this.status = status;
    refreshView();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_accounts:
        status = AppStatus.GETTING_ACCOUNT_ID;
        chooseDeviceAccount();
        return true;
      default:
        throw new UnsupportedOperationException("Not implemented");
    }
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState.containsKey("publisherAccountId")) {
      publisherAccountId = savedInstanceState.getString("publisherAccountId");
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    // Serialize the current dropdown position.
    outState.putString("publisherAccountId", publisherAccountId);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode == Activity.RESULT_OK) {
          haveGooglePlayServices();
        } else {
          checkGooglePlayServicesAvailable();
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == Activity.RESULT_OK) {
          publisherAccountId = null;
          status = AppStatus.GETTING_ACCOUNT_ID;
          refreshView();
        } else {
          chooseDeviceAccount();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            apiController.setAccountName(accountName);
            refreshView();
          }
        }
        break;
      default:
        break;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    fragmentManager = getSupportFragmentManager();
    apiController = ApiController.getApiController(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setContentView(R.layout.activity_main);

    apiController = ApiController.getApiController(this);

    // Set up the action bar to show a dropdown list.
    final ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    // Set up the dropdown list navigation in the action bar.
    actionBar.setListNavigationCallbacks(
    // Specify a SpinnerAdapter to populate the dropdown list.
        new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1,
            android.R.id.text1, new String[] {getString(R.string.title_section1),
                getString(R.string.title_section2), getString(R.string.title_section3)}), this);
  }

  private void pickPublisherAccount() {
    // Show account picker.
    final List<CharSequence> items = new ArrayList<CharSequence>();
    for (Account account : apiController.getAccounts()) {
      items.add(account.getName());
    }
    // TODO(jalc): Is this really the best way?
    CharSequence[] itemsArray = items.toArray(new CharSequence[items.size()]);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Select the AdSense account");
    builder.setSingleChoiceItems(itemsArray, -1, null);
    builder.setPositiveButton("Ok", new OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
        if (selectedPosition == -1) {
          return;
        }
        publisherAccountId = apiController.getAccounts().get(selectedPosition).getId();

        getActionBar().setSelectedNavigationItem(0);
        status = AppStatus.FETCHING_INVENTORY;
        refreshView();
      }
    });
    AlertDialog d = builder.create();
    d.show();
  }

  private void refreshView() {
    Log.d(TAG, String.format("refreshView status: %s", status.toString()));
    switch (status) {
      case SHOWING_INVENTORY:
        createInventoryFragment();
        return;
      case PICKING_ACCOUNT:
        pickPublisherAccount();
        return;
      case SHOWING_CUSTOM_CONFIG:
        createCustomReportConfigFragment();
        return;
      case FETCHING_SIMPLE_REPORT:
        generateSimpleReport();
        return;
      case FETCHING_REPORT:
        return;
      case SHOWING_REPORT:
        createCustomReportFragment();
        return;
      case FETCHING_METADATA:
        apiController.reset();
        fromDate = null;
        toDate = null;
        apiController.loadMetadata();
        showBlankFragment();
        break;
      case GETTING_ACCOUNT_ID:
        apiController.loadAccounts();
        showBlankFragment();
        break;
      case FETCHING_INVENTORY:
        showBlankFragment();
        apiController.loadInventory(publisherAccountId);
        break;
      default:
        showBlankFragment();
        return;
    }
  }

  private void showBlankFragment() {
    Fragment fragment = new DummySectionFragment();
    Bundle args = new Bundle();
    args.putString("status", status.toString());
    fragment.setArguments(args);
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.container, fragment).addToBackStack(null).commit();
  }

  private void generateSimpleReport() {
    List<String> dimensions = new ArrayList<String>();
    List<String> metrics = new ArrayList<String>();

    dimensions.add("MONTH");
    metrics.add("EARNINGS");
    String fromDate = "today-6m";
    String toDate = "today";
    apiController.loadReport(publisherAccountId, fromDate, toDate, dimensions, metrics);
  }

  private void haveGooglePlayServices() {
    // Check if there is already an account selected.
    if (apiController.getCredential().getSelectedAccountName() == null) {
      // Ask user to choose account.
      chooseDeviceAccount();
    } else {
      refreshView();
    }
  }

  private void chooseDeviceAccount() {
    startActivityForResult(apiController.getCredential().newChooseAccountIntent(),
        REQUEST_ACCOUNT_PICKER);
  }

  /**
   * Check that Google Play services APK is installed and up to date.
   */
  private boolean checkGooglePlayServicesAvailable() {
    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    }
    return true;
  }

  private void createCustomReportConfigFragment() {

    CustomReportConfigFragment reportConfig = new CustomReportConfigFragment();
    // Tell the fragment to come back to the activity with the result.
    reportConfig.setUIController(this);
    fragmentManager.beginTransaction().replace(R.id.container, reportConfig).commit();
  }

  private void createCustomReportFragment() {
    DisplayReportFragment fragment = new DisplayReportFragment();
    fragment.setUIController(this);
    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null)
        .commit();
  }

  private void createInventoryFragment() {
    DisplayInventoryFragment fragment = new DisplayInventoryFragment();
    fragment.setUIController(this);
    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
  }

}
