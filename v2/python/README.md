# AdSense Management API v1.4 Samples

A collection of command-line samples for the AdSense Management API.

##Installation and first request

1. Download Google APIs Client Library for Python (google-api-python-client) and
   the OAuth library
   (https://github.com/googleapis/google-auth-library-python-oauthlib):
  - https://code.google.com/p/google-api-python-client/
  - https://github.com/googleapis/google-auth-library-python-oauthlib

  or use pip:

  ```bash
  $ pip install google-api-python-client
  $ pip install google_auth_oauthlib
  ```

2. (No longer needed, check on this) Make sure you can import the client library:

  ```
  $ python
  >>> import apiclient
  ```

3. Execute any of the scripts to begin the auth flow:

  ```bash
  $ python get_all_accounts.py
  ```

  A browser window will open and ask you to login. Use the AdSense account.

  Note: some examples require an argument to be passed in in order to run,
  e.g., `python get_all_ad_units.py --ad_client_id
  accounts/{accountId}/adclients/{adClientId}`.

4. Accept the permissions dialog. The browser should display

  `The authentication flow has completed.`

  Close the window and go back to the shell.

5. The `get_all_accounts.py` script will output:

  `Account with ID "pub-1234567890123456" and name "My account" was found.`

6. The tokens will be stored in adsense.dat by default.

  Remove this file to restart the auth flow. If you don't want to store your
  OAuth credentials, you can set `ALWAYS_REQUIRE_AUTHENTICATION = True` in
  `adsense_utils.py`. Keep in mind that this means that every time you run an
  example, you will have to go to a browser to complete the OAuth flow.
