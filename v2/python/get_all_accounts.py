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
#Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This example gets all accounts for the logged in user.

Tags: accounts.list
"""

import adsense_util
import sys
import google.auth.exceptions
from googleapiclient import discovery

# The max page size when fetching data from the API.
MAX_PAGE_SIZE = 50


def main(argv):
  # Authenticate and construct service.
  credentials = adsense_util.get_adsense_credentials()
  with discovery.build('adsense', 'v2', credentials = credentials) as service:
    try:
      # Retrieve account list in pages and display data as we receive it.
      request = service.accounts().list(pageSize=MAX_PAGE_SIZE)

      while request is not None:
        result = request.execute()
        accounts = result['accounts']

        for account in accounts:
          print ('Account with ID "%s" and name "%s" was found. '
                 % (account['name'], account['displayName']))

        request = service.accounts().list_next(request, result)

    except google.auth.exceptions.RefreshError:
      print('The credentials have been revoked or expired, please delete the '
            '"%s" file and re-run the application to re-authorize.' %
            adsense_util.CREDENTIALS_FILE)

if __name__ == '__main__':
  main(sys.argv)
