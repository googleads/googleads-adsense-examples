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

"""Utility class to handle multiple accounts per login.
"""

DATE_FORMAT = '%Y-%m-%d'
MONTH_FORMAT = '%Y-%m'


def get_account_id(service):
  """Gets the AdSense account id, letting the user choose if multiple exist.

  Args:
    service: the Adsense service used to fetch the accounts.

  Returns:
    The selected account id.
  """
  account_id = None
  accounts = service.accounts().list().execute()
  if len(accounts['items']) == 1:
    account_id = accounts['items'][0]['id']
  else:
    print 'Multiple accounts were found. Please choose:'
    for i, account in enumerate(accounts['items']):

      print ' %d) %s (%s)' % (i + 1, account['name'], account['id'])
    selection = (raw_input('Please choose number 1-%d>'
                           % (len(accounts['items']))))
    account_id = accounts['items'][int(selection) - 1]['id']
  return account_id

