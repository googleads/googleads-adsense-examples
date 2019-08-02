# AdSense Management API sample for PHP

This sample runs a number of different requests against the AdSense Management
API.

## Prerequisites

* PHP version 5.2.1 or greater
* The JSON PHP extension


## Installation

* Download and install the [PHP Client library for Google APIs](
    https://developers.google.com/api-client-library/php/start/installation).
* Copy the AdSense Management API sample for PHP to your server.
* Change the include path in adsense-sample.php to your vendor directory if
  needed.
* Modify `client_secrets.json` with your client ID, client secret and redirect
  URL (`http://your/path/adsense-sample.php`).
* (Optional) If you want to store credentials between runs to avoid authorizing
  more than once, change `STORE_ON_DISK` in adsense-sample.php to `true`.
  * You may have to give your PHP installation write permissions to the token
    file. One easy way of doing this is creating an empty `tokens.dat` file in
    the installation directory and making it writeable by your web server.
* Open the sample (`http://your/path/adsense-sample.php`) in your browser.

This will start an authentication flow, redirect back to your server, and then
print data about your AdSense account.
