#!/usr/bin/python
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

"""This example gets a specific account for the logged in user.

This includes the full tree of sub-accounts.

Tags: accounts.get, accounts.listChildAccounts
"""

import inspect

import adsense_util
import sys
import google.auth.exceptions
from googleapiclient import discovery


def main(argv):
  # Authenticate and construct service.
  credentials = adsense_util.get_adsense_credentials()
  with discovery.build('adsense', 'v2', credentials = credentials) as service:
    try:
      # Select and retrieve top-level account.
      account_id = adsense_util.get_account_id(service)
      request = service.accounts().get(name=account_id)
      account = request.execute()

      # If the account exists, recursivley display its child accounts.
      if account:
        display_tree(service, account)

    except google.auth.exceptions.RefreshError:
      print('The credentials have been revoked or expired, please delete the '
            '"%s" file and re-run the application to re-authorize.' %
            adsense_util.CREDENTIALS_FILE)


def display_tree(service, account, level=0):
  print (' ' * level * 2 +
         'Account with ID "%s" and name "%s" was found. ' %
         (account['name'], account['displayName']))

  request = service.accounts().listChildAccounts(parent=account['name'])
  sub_accounts = request.execute()
  for sub_account in sub_accounts:
    display_tree(service, sub_account, level + 1)


if __name__ == '__main__':
  main(sys.argv)
