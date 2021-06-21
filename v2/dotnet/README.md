# AdSense Management API v2 .NET Samples

## Prerequisites
* Project with access to the AdSense Management API in http://console.developers.google.com
* NuGet package management
* Check the [Google API .NET
  Client](https://github.com/googleapis/google-api-dotnet-client) to ensure
  you're using a supported .NET framework.

## Installation
* Use NuGet to install **Google.Apis.Adsense.v2 Client Library**. See the [NuGet
  packet page](https://www.nuget.org/packages/Google.Apis.Adsense.v2/) for more
  details.

## Register Your Application
  1. Visit the [Google Cloud console](https://cloud.google.com/console/start/api?id=adsense)
  1. If necessary, sign in to your Google Account, select or create a project,
     and agree to the terms of service.  Click Continue.
  1. From the
     [API Library](https://console.cloud.google.com/start/api?id=adsense.googleapis.com),
     enable the **AdSense Management API**.
  1. Click on **APIs & Services > Credentials** in the left navigation menu.
  1. Click **CREATE CREDENTIALS > OAuth client ID**.
  1. Select **Desktop app** as the application type, give it a name, then click
     **Create**.
  1. From the Credentials page, click **Download JSON** next to the client ID you
     just created.
  1. Using the client_id and client_secret from that JSON file, modify AdSenseSample.cs
     to include your `ClientId` and `ClientSecret`.

## Run the Examples
* Build and run AdSenseSample.cs, which runs various examples against the
  AdSense Management API. For examples, if you are using .NET Core, you
  can use the command `dotnet run AdSenseSample.cs`.
* The executable file will start an auth flow and then print data about the AdSense account.
