# AdSense Management API sample for PHP

This sample runs a number of different requests against the AdSense Management
API.

## Prerequisites

* PHP version 5.2.1 or greater
* The JSON PHP extension


## Installation

* Download and install the [PHP Client library for Google APIs](
    https://developers.google.com/api-client-library/php/start/installation)
* Copy the AdSense Management API sample for PHP to your server
* Change the include path in adsense-sample.php to your client
  library installation
* Modify client_secrets.json with your client ID, client secret and redirect URL
  (http://your/path/adsense-sample.php)
* Open the sample (http://your/path/adsense-sample.php) in your browser

This will start an authentication flow, redirect back to your server, and then
print data about your AdSense account.
