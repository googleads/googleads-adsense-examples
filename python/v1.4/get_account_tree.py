#!/usr/bin/python
#
# Copyright 2014 Google Inc. All Rights Reserved.
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

Tags: accounts.get
"""

__author__ = 'jalc@google.com (Jose Alcerreca)'

import sys
from apiclient import sample_tools
from oauth2client import client
from adsense_util import get_account_id


def main(argv):
  # Authenticate and construct service.
  service, _ = sample_tools.init(
      argv, 'adsense', 'v1.4', __doc__, __file__, parents=[],
      scope='https://www.googleapis.com/auth/adsense.readonly')

  try:
    account_id = get_account_id(service)
    # Retrieve account.
    request = service.accounts().get(accountId=account_id, tree=True)
    account = request.execute()

    if account:
      display_tree(account)

  except client.AccessTokenRefreshError:
    print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')


def display_tree(account, level=0):
  print (' ' * level * 2 +
         'Account with ID "%s" and name "%s" was found. ' %
         (account['id'], account['name']))

  if 'subAccounts' in account:
    for sub_account in account['subAccounts']:
      display_tree(sub_account, level + 1)

if __name__ == '__main__':
  main(sys.argv)
