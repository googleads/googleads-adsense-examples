# AdSense Management API v1.4 Samples

A collection of command-line samples for the AdSense Management API.

##Installation and first request

1. Download Google APIs Client Library for Python (google-api-python-client):
  https://code.google.com/p/google-api-python-client/

  or use pip:

  ```bash
  $ pip install google-api-python-client
  ```

2. Make sure you can import the client library:

  ```
  $ python
  >>> import apiclient
  ```

3. Execute any of the scripts to begin the auth flow:

  ```bash
  $ python get_all_accounts.py
  ```

  A browser window will open and ask you to login. Use the AdSense account.

4. Accept the permissions dialog. The browser should display

  `The authentication flow has completed.`

  Close the window and go back to the shell.

5. The script will output:

  `Account with ID "pub-1234567890123456" and name "My account" was found.`

6. The tokens will be stored in adsense.dat.

  Remove this file to restart the auth flow.
