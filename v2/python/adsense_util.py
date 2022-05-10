#
# Copyright 2021 Google LLC.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Utility class to handle account and credential management."""

from google_auth_oauthlib.flow import InstalledAppFlow
from google.oauth2.credentials import Credentials
from google.auth.exceptions import RefreshError
import json
import os

# The CLIENT_SECRETS_FILE variable specifies the name of a file that contains
# the OAuth 2.0 information for this application, including its client_id and
# client_secret.
CLIENT_SECRETS_FILE = 'client_secrets.json'

# The CREDENTIALS_FILE variable specified the name of the file that contains
# the access token for making API requests after this application has been
# approved to access your AdSense account. To prevent the access token from
# being stored, set ALWAYS_REQUIRE_AUTHENTICATION to True, which will cause
# the OAuth flow to be run every time an API request is made.
CREDENTIALS_FILE = 'adsense.dat'
ALWAYS_REQUIRE_AUTHENTICATION = False

# This access scope grants read-only access to the authenticated user's account.
SCOPES = ['https://www.googleapis.com/auth/adsense.readonly']

def get_account_id(service):
  """Gets the AdSense account id, letting the user choose if multiple exist.

  Args:
    service: the Adsense service used to fetch the accounts.

  Returns:
    The selected account id.
  """
  account_id = None
  response = service.accounts().list().execute()
  if len(response['accounts']) == 1:
    account_id = response['accounts'][0]['name']
  else:
    print('Multiple accounts were found. Please choose:')
    for i, account in enumerate(response['accounts']):
      print(' %d) %s (%s)' % (i + 1, account['displayName'], account['name']))
    selection = (input('Please choose number 1-%d>'
                           % (len(response['accounts']))))
    account_id = response['accounts'][int(selection) - 1]['name']
  return account_id

def get_adsense_credentials(overwrite_existing_credentials=False):
  """Gets AdSense credentials (locally cached or by running the OAuth flow).

  Args:
    overwrite_existing_credentials: when True, force a refresh of credentials
        by running OAuth flow

  Returns:
    A credential object required for making authenticated API requests.
  """
  credentials = None
  if (os.path.isfile(CREDENTIALS_FILE) and not overwrite_existing_credentials
      and not ALWAYS_REQUIRE_AUTHENTICATION ):
    credentials = Credentials.from_authorized_user_file(CREDENTIALS_FILE)
  else:
    flow = InstalledAppFlow.from_client_secrets_file(CLIENT_SECRETS_FILE, SCOPES)
    credentials = flow.run_local_server()
    with open(CREDENTIALS_FILE, 'w') as credentials_file:
      credentials_json = credentials.to_json()
      if isinstance(credentials_json, str):
        credentials_json = json.loads(credentials_json)
      json.dump(credentials_json, credentials_file)
  return credentials
