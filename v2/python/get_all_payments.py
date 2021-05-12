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

"""Gets all payments available for the logged in user's default account.

Tags: accounts.payments.list
"""

import adsense_util
import datetime
import sys
import google.auth.exceptions
from googleapiclient import discovery


def main(argv):
  # Authenticate and construct service.
  credentials = adsense_util.get_adsense_credentials()
  with discovery.build('adsense', 'v2', credentials = credentials) as service:
    try:
      # Select and retrieve account.
      account_id = adsense_util.get_account_id(service)
      # Retrieve ad client list in pages and display data as we receive it.
      request = service.accounts().payments().list(parent=account_id)

      if request is not None:
        result = request.execute()
        if 'payments' in result:
          for payment in result['payments']:
            if 'date' in payment:
              payment_date = datetime.date(
                  payment['date']['year'],
                  payment['date']['month'],
                  payment['date']['day']).strftime('%Y-%m-%d')
            else:
              payment_date = 'unknown'
            print('Payment with ID "%s" of %s and date %s was found' % (
                  payment['name'], payment['amount'], payment_date))
        else:
          print('No payments found.')

    except google.auth.exceptions.RefreshError:
      print('The credentials have been revoked or expired, please delete the '
            '"%s" file and re-run the application to re-authorize.' %
            adsense_util.CREDENTIALS_FILE)

if __name__ == '__main__':
  main(sys.argv)
