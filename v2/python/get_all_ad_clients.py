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

"""This example gets all ad clients for the logged in user's account.

Tags: accounts.adclients.list
"""

import adsense_util
import sys
import google.auth.exceptions
from googleapiclient import discovery

MAX_PAGE_SIZE = 50


def main(argv):
  # Authenticate and construct service.
  credentials = adsense_util.get_adsense_credentials()
  with discovery.build('adsense', 'v2', credentials = credentials) as service:
    try:
      # Select and retrieve account.
      account_id = adsense_util.get_account_id(service)
      # Retrieve ad client list in pages and display data as we receive it.
      request = service.accounts().adclients().list(
          parent=account_id, pageSize=MAX_PAGE_SIZE)

      while request is not None:
        result = request.execute()

        if 'adClients' in result:
          ad_clients = result['adClients']
          for ad_client in ad_clients:
            print('Ad client for product "%s" with ID "%s" was found.' % (
                  ad_client.get('productCode', 'N/A'), ad_client['name']))
            print('\tSupports reporting: %s' %
                  ('Yes' if ad_client.get('reportingDimensionId') else 'No'))
        else:
          print('No ad clients were found.')

        request = service.accounts().adclients().list_next(request, result)

    except google.auth.exceptions.RefreshError:
      print('The credentials have been revoked or expired, please delete the '
            '"%s" file and re-run the application to re-authorize.' %
            adsense_util.CREDENTIALS_FILE)


if __name__ == '__main__':
  main(sys.argv)
