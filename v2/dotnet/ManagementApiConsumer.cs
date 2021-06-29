/*
Copyright 2021 Google Inc

Licensed under the Apache License, Version 2.0(the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

using System;
using System.Collections.Generic;
using System.Linq;

using Google.Apis.Adsense.v2;
using Google.Apis.Adsense.v2.Data;
using DimensionsEnum =
    Google.Apis.Adsense.v2.AccountsResource.ReportsResource.GenerateRequest.DimensionsEnum;
using MetricsEnum =
    Google.Apis.Adsense.v2.AccountsResource.ReportsResource.GenerateRequest.MetricsEnum;
using SavedDateRangeEnum = Google.Apis.Adsense.v2.AccountsResource.ReportsResource.SavedResource
                               .GenerateRequest.DateRangeEnum;

namespace AdSense.Sample {
  /// <summary>
  /// A sample consumer that runs multiple requests against the AdSense Management API.
  /// These include:
  /// <list type="bullet">
  /// <item>
  /// <description>Retrieves the list of accounts</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of ad clients</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of ad units for a random ad client</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of custom channels for a random ad unit</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of custom channels</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of ad units tagged by a random custom channel</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of URL channels for the logged in user</description>
  /// </item>
  /// <item>
  /// <description>Retrieves the list of saved reports for the logged in user</description>
  /// </item>
  /// <item>
  /// <description>Generates a random saved report</description>
  /// </item>
  /// <item>
  /// <description>Generates a ad-hoc report</description>
  /// </item>
  /// </list>
  /// </summary>
  public class ManagementApiConsumer {
    private AdsenseService service;
    private int maxListPageSize;
    private Account adSenseAccount;

    /// <summary>Initializes a new instance of the <see cref="ManagementApiConsumer"/>
    /// class.</summary> <param name="service">AdSense service object on which to run the
    /// requests.</param> <param name="maxListPageSize">The maximum page size to retrieve.</param>
    public ManagementApiConsumer(AdsenseService service, int maxListPageSize) {
      this.service = service;
      this.maxListPageSize = maxListPageSize;
    }

    /// <summary>Runs multiple Publisher requests against the AdSense Management API.</summary>
    internal void RunCalls() {
      IList<Account> accounts = GetAllAccounts();

      // Get an example account, so we can run the following samples.
      adSenseAccount = accounts.NullToEmpty().FirstOrDefault();
      if (adSenseAccount != null) {
        DisplayAccountTree(adSenseAccount.Name);
      }

      var adClients = GetAllAdClients();

      // Get an ad client, so we can run the rest of the samples.
      var exampleAdClient = adClients.NullToEmpty().FirstOrDefault();
      if (exampleAdClient != null) {
        var adUnits = GetAllAdUnits(exampleAdClient.Name);

        // Get an example ad unit, so we can run the following sample.
        var exampleAdUnit = adUnits.NullToEmpty().FirstOrDefault();
        if (exampleAdUnit != null) {
          DisplayAllCustomChannelsForAdUnit(exampleAdUnit.Name);
        }

        var customChannels = GetAllCustomChannels(exampleAdClient.Name);

        // Get an example custom channel, so we can run the following sample.
        var exampleCustomChannel = customChannels.NullToEmpty().FirstOrDefault();
        if (exampleCustomChannel != null) {
          DisplayAllAdUnitsForCustomChannel(exampleCustomChannel.Name);
        }

        DisplayAllUrlChannels(exampleAdClient.Name);

        IList<SavedReport> savedReports = GetAllSavedReports();

        // Get an example saved report, so we can run the following sample.
        var exampleSavedReport = savedReports.NullToEmpty().FirstOrDefault();
        if (exampleSavedReport != null) {
          GenerateSavedReport(exampleSavedReport.Name);
        }

        GenerateReport(exampleAdClient.Name);
      }

      DisplayAllAlerts();
    }

    /// <summary>Gets and prints all accounts for the logged in user.</summary>
    /// <returns>The last page of retrieved accounts.</returns>
    private IList<Account> GetAllAccounts() {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all AdSense accounts");
      Console.WriteLine("=================================================================");

      // Retrieve account list in pages and display data as we receive it.
      string pageToken = null;
      ListAccountsResponse accountResponse = null;

      do {
        var accountRequest = service.Accounts.List();
        accountRequest.PageSize = maxListPageSize;
        accountRequest.PageToken = pageToken;
        accountResponse = accountRequest.Execute();

        if (!accountResponse.Accounts.IsNullOrEmpty()) {
          foreach (var account in accountResponse.Accounts) {
            Console.WriteLine("Account with ID \"{0}\" and name \"{1}\" was found.", account.Name,
                              account.DisplayName);
          }
        } else {
          Console.WriteLine("No accounts found.");
        }

        pageToken = accountResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();

      // Return the last page of accounts, so that the main sample has something to run.
      return accountResponse.Accounts;
    }

    /// <summary>Displays the AdSense account tree for a given account.</summary>
    /// <param name="accountId">The ID for the account to be used.</param>
    private void DisplayAccountTree(string accountId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Displaying AdSense account tree for {0}", accountId);
      Console.WriteLine("=================================================================");

      // Retrieve account.
      var account = service.Accounts.Get(accountId).Execute();
      DisplayTree(account, 0);

      Console.WriteLine();
    }

    /// <summary>
    /// Auxiliary method to recurse through the account tree, displaying it.
    /// </summary>
    /// <param name="parentAccount">The account to print a sub-tree for.</param>
    /// <param name="level">The depth at which the top account exists in the tree.</param>
    private void DisplayTree(Account parentAccount, int level) {
      Console.WriteLine("{0}Account with ID \"{1}\" and name \"{2}\" was found.",
                        new string(' ', 2 * level), parentAccount.Name, parentAccount.DisplayName);

      var subAccounts = service.Accounts.ListChildAccounts(parentAccount.Name).Execute().Accounts;

      if (!subAccounts.IsNullOrEmpty()) {
        foreach (var subAccount in subAccounts) {
          DisplayTree(subAccount, level + 1);
        }
      }
    }

    /// <summary>
    /// Gets and prints all ad clients for the logged in user's default account.
    /// </summary>
    /// <returns>The last page of retrieved accounts.</returns>
    private IList<AdClient> GetAllAdClients() {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all ad clients for default account");
      Console.WriteLine("=================================================================");

      // Retrieve ad client list in pages and display data as we receive it.
      string pageToken = null;
      ListAdClientsResponse adClientResponse = null;

      do {
        var adClientRequest = service.Accounts.Adclients.List(adSenseAccount.Name);
        adClientRequest.PageSize = maxListPageSize;
        adClientRequest.PageToken = pageToken;
        adClientResponse = adClientRequest.Execute();

        if (!adClientResponse.AdClients.IsNullOrEmpty()) {
          foreach (var adClient in adClientResponse.AdClients) {
            Console.WriteLine("Ad client for product \"{0}\" with ID \"{1}\" was found.",
                              adClient.ProductCode, adClient.Name);
            Console.WriteLine("\tSupports reporting: {0}",
                              String.IsNullOrEmpty(adClient.ReportingDimensionId) ? "No" : "Yes");
          }
        } else {
          Console.WriteLine("No ad clients found.");
        }

        pageToken = adClientResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();

      // Return the last page of ad clients, so that the main sample has something to run.
      return adClientResponse.AdClients;
    }

    /// <summary>
    /// Gets and prints all ad units in an ad client.
    /// </summary>
    /// <param name="adClientId">The ID for the ad client to be used.</param>
    /// <returns>The last page of retrieved accounts.</returns>
    private IList<AdUnit> GetAllAdUnits(string adClientId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all ad units for ad client {0}", adClientId);
      Console.WriteLine("=================================================================");

      // Retrieve ad client list in pages and display data as we receive it.
      string pageToken = null;
      ListAdUnitsResponse adUnitResponse = null;

      do {
        var adUnitRequest = service.Accounts.Adclients.Adunits.List(adClientId);
        adUnitRequest.PageSize = maxListPageSize;
        adUnitRequest.PageToken = pageToken;
        adUnitResponse = adUnitRequest.Execute();

        if (!adUnitResponse.AdUnits.IsNullOrEmpty()) {
          foreach (var adUnit in adUnitResponse.AdUnits) {
            Console.WriteLine("Ad unit with ID \"{0}\", name \"{1}\" and state \"{2}\" was found.",
                              adUnit.Name, adUnit.DisplayName, adUnit.State);
          }
        } else {
          Console.WriteLine("No ad units found.");
        }

        pageToken = adUnitResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();

      // Return the last page of ad units, so that the main sample has something to run.
      return adUnitResponse.AdUnits;
    }

    /// <summary>
    /// Gets and prints all custom channels in an ad client.
    /// </summary>
    /// <param name="adClientId">The ID for the ad client to be used.</param>
    /// <returns>The last page of custom channels.</returns>
    private IList<CustomChannel> GetAllCustomChannels(string adClientId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all custom channels for ad client {0}", adClientId);
      Console.WriteLine("=================================================================");

      // Retrieve custom channel list in pages and display data as we receive it.
      string pageToken = null;
      ListCustomChannelsResponse customChannelResponse = null;

      do {
        var customChannelRequest = service.Accounts.Adclients.Customchannels.List(adClientId);
        customChannelRequest.PageSize = maxListPageSize;
        customChannelRequest.PageToken = pageToken;
        customChannelResponse = customChannelRequest.Execute();

        if (!customChannelResponse.CustomChannels.IsNullOrEmpty()) {
          foreach (var customChannel in customChannelResponse.CustomChannels) {
            Console.WriteLine("Custom channel with ID \"{0}\" and name \"{1}\" was found.",
                              customChannel.Name, customChannel.DisplayName);
          }
        } else {
          Console.WriteLine("No custom channels found.");
        }

        pageToken = customChannelResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();

      // Return the last page of custom channels, so that the main sample has something to run.
      return customChannelResponse.CustomChannels;
    }

    /// <summary>
    /// Prints all ad units corresponding to a specified custom channel.
    /// </summary>
    /// <param name="customChannelId">The ID for the custom channel to be used.</param>
    private void DisplayAllAdUnitsForCustomChannel(string customChannelId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all ad units for custom channel {0}", customChannelId);
      Console.WriteLine("=================================================================");

      // Retrieve ad client list in pages and display data as we receive it.
      string pageToken = null;
      ListLinkedAdUnitsResponse adUnitResponse = null;

      do {
        var adUnitRequest =
            service.Accounts.Adclients.Customchannels.ListLinkedAdUnits(customChannelId);
        adUnitRequest.PageSize = maxListPageSize;
        adUnitRequest.PageToken = pageToken;
        adUnitResponse = adUnitRequest.Execute();

        if (!adUnitResponse.AdUnits.IsNullOrEmpty()) {
          foreach (var adUnit in adUnitResponse.AdUnits) {
            Console.WriteLine("Ad unit with ID \"{0}\", name \"{1}\" and state \"{2}\" was found.",
                              adUnit.Name, adUnit.DisplayName, adUnit.State);
          }
        } else {
          Console.WriteLine("No ad units found.");
        }

        pageToken = adUnitResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();
    }

    /// <summary>Displays all custom channels an ad unit has been added to.</summary>
    /// <param name="adUnitId">The ID for the ad unit to be used.</param>
    private void DisplayAllCustomChannelsForAdUnit(string adUnitId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all custom channels for ad unit {0}", adUnitId);
      Console.WriteLine("=================================================================");

      // Retrieve custom channel list in pages and display data as we receive it.
      string pageToken = null;
      ListLinkedCustomChannelsResponse customChannelResponse = null;

      do {
        var customChannelRequest =
            service.Accounts.Adclients.Adunits.ListLinkedCustomChannels(adUnitId);
        customChannelRequest.PageSize = maxListPageSize;
        customChannelRequest.PageToken = pageToken;
        customChannelResponse = customChannelRequest.Execute();

        if (!customChannelResponse.CustomChannels.IsNullOrEmpty()) {
          foreach (var customChannel in customChannelResponse.CustomChannels) {
            Console.WriteLine("Custom channel with ID \"{0}\" and name \"{1}\" was found.",
                              customChannel.Name, customChannel.DisplayName);
          }
        } else {
          Console.WriteLine("No custom channels found.");
        }

        pageToken = customChannelResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();
    }

    /// <summary>Displays all URL channels in an ad client.</summary>
    /// <param name="adClientId">The ID for the ad client to be used.</param>
    private void DisplayAllUrlChannels(string adClientId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all URL channels for ad client {0}", adClientId);
      Console.WriteLine("=================================================================");

      // Retrieve URL channel list in pages and display data as we receive it.
      string pageToken = null;
      ListUrlChannelsResponse urlChannelResponse = null;

      do {
        var urlChannelRequest = service.Accounts.Adclients.Urlchannels.List(adClientId);
        urlChannelRequest.PageSize = maxListPageSize;
        urlChannelRequest.PageToken = pageToken;
        urlChannelResponse = urlChannelRequest.Execute();

        if (!urlChannelResponse.UrlChannels.IsNullOrEmpty()) {
          foreach (var urlChannel in urlChannelResponse.UrlChannels) {
            Console.WriteLine("URI channel with pattern \"{0}\" was found.", urlChannel.UriPattern);
          }
        } else {
          Console.WriteLine("No URL channels found.");
        }

        pageToken = urlChannelResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();
    }

    /// <summary>Retrieves a report, using a filter for a specified ad client.</summary>
    /// <param name="adClientId">The ID for the ad client to be used.</param>
    private void GenerateReport(string adClientId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Running report for ad client {0}", adClientId);
      Console.WriteLine("=================================================================");

      // Prepare report.
      var reportRequest = service.Accounts.Reports.Generate(adSenseAccount.Name);

      // Specify the dates and desired ad client using a filter, as well as other parameters.
      reportRequest.StartDateYear = 2021;
      reportRequest.StartDateMonth = 3;
      reportRequest.StartDateDay = 1;
      reportRequest.EndDateYear = 2021;
      reportRequest.EndDateMonth = 3;
      reportRequest.EndDateDay = 31;
      reportRequest.Filters =
          new List<string> { "AD_CLIENT_ID==" + ReportUtils.EscapeFilterParameter(adClientId) };

      reportRequest.AddMetric(MetricsEnum.PAGEVIEWS);
      reportRequest.AddMetric(MetricsEnum.ADREQUESTS);
      reportRequest.AddMetric(MetricsEnum.ADREQUESTSCOVERAGE);
      reportRequest.AddMetric(MetricsEnum.ADREQUESTSCTR);
      reportRequest.AddMetric(MetricsEnum.COSTPERCLICK);
      reportRequest.AddMetric(MetricsEnum.ADREQUESTSRPM);
      reportRequest.AddMetric(MetricsEnum.ESTIMATEDEARNINGS);

      reportRequest.AddDimension(DimensionsEnum.DATE);

      reportRequest.OrderBy = new List<string> { "+DATE" };

      // Run report.
      var reportResponse = reportRequest.Execute();

      if (!reportResponse.Rows.IsNullOrEmpty()) {
        ReportUtils.DisplayHeaders(reportResponse.Headers);
        Console.WriteLine("Showing data from {0} to {1}", reportResponse.StartDate,
                          reportResponse.EndDate);
        ReportUtils.DisplayRows(reportResponse.Rows);
      } else {
        Console.WriteLine("No rows returned.");
      }

      Console.WriteLine();
    }

    /// <summary>
    /// Retrieves a report, using a filter for a specified saved report.
    /// </summary>
    /// <param name="savedReportId">The ID of the saved report to generate.</param>
    private void GenerateSavedReport(string savedReportId) {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Running saved report {0}", savedReportId);
      Console.WriteLine("=================================================================");

      var savedReportRequest = service.Accounts.Reports.Saved.Generate(savedReportId);
      savedReportRequest.DateRange = SavedDateRangeEnum.LAST7DAYS;
      ReportResult savedReportResponse = savedReportRequest.Execute();

      // Run report.
      if (!savedReportResponse.Rows.IsNullOrEmpty()) {
        ReportUtils.DisplayHeaders(savedReportResponse.Headers);
        ReportUtils.DisplayRows(savedReportResponse.Rows);
      } else {
        Console.WriteLine("No rows returned.");
      }

      Console.WriteLine();
    }

    /// <summary>
    /// Gets and prints all the saved reports for the logged in user's default account.
    /// </summary>
    /// <returns>The last page of the retrieved saved reports.</returns>
    private IList<SavedReport> GetAllSavedReports() {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all saved reports");
      Console.WriteLine("=================================================================");

      // Retrieve ad client list in pages and display data as we receive it.
      string pageToken = null;
      ListSavedReportsResponse savedReportResponse = null;

      do {
        var savedReportRequest = service.Accounts.Reports.Saved.List(adSenseAccount.Name);
        savedReportRequest.PageSize = maxListPageSize;
        savedReportRequest.PageToken = pageToken;
        savedReportResponse = savedReportRequest.Execute();

        if (!savedReportResponse.SavedReports.IsNullOrEmpty()) {
          foreach (var savedReport in savedReportResponse.SavedReports) {
            Console.WriteLine("Saved report with ID \"{0}\" and title \"{1}\" was found.",
                              savedReport.Name, savedReport.Title);
          }
        } else {
          Console.WriteLine("No saved saved reports found.");
        }

        pageToken = savedReportResponse.NextPageToken;
      } while (pageToken != null);
      Console.WriteLine();
      return savedReportResponse.SavedReports;
    }

    /// <summary>Prints all the alerts for the logged in user's default account.</summary>
    private void DisplayAllAlerts() {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all alerts");
      Console.WriteLine("=================================================================");

      ListAlertsResponse alertsResponse =
          service.Accounts.Alerts.List(adSenseAccount.Name).Execute();

      if (!alertsResponse.Alerts.IsNullOrEmpty()) {
        foreach (var alert in alertsResponse.Alerts) {
          Console.WriteLine("Alert with ID \"{0}\" type \"{1}\" and severity \"{2}\" was found.",
                            alert.Name, alert.Type, alert.Severity);
        }
      } else {
        Console.WriteLine("No alerts found.");
      }

      Console.WriteLine();
    }

    /// <summary>Prints all the alerts for the logged in user's default account.</summary>
    private void DisplayAllPayments() {
      Console.WriteLine("=================================================================");
      Console.WriteLine("Listing all payments");
      Console.WriteLine("=================================================================");

      ListPaymentsResponse paymentsResponse =
          service.Accounts.Payments.List(adSenseAccount.Name).Execute();

      if (!paymentsResponse.Payments.IsNullOrEmpty()) {
        foreach (var payment in paymentsResponse.Payments) {
          Console.WriteLine("Payment with ID \"{0}\" of {1} and date \"{2}\" was found.",
                            payment.Name, payment.Amount, payment.Date);
        }
      } else {
        Console.WriteLine("No payments found.");
      }

      Console.WriteLine();
    }
  }
}
