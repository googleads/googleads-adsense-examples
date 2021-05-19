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

"""This example gets all ad units corresponding to a specified custom channel.

To get custom channels, run get_all_custom_channels.py.

Tags: accounts.adclients.customchannels.listLinkedAdUnits
"""

import adsense_util
import argparse
import sys
import google.auth.exceptions
from googleapiclient import discovery


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=True)
argparser.add_argument(
    '--custom_channel_id',
    help='The ID of the custom channel for which to fetch ad units. Format: '
         '"accounts/{account}/adclients/{adclient}/customchannels/'
         '{customchannel}".')

args = argparser.parse_args()
custom_channel_id = args.custom_channel_id

# The max page size when fetching data from the API.
MAX_PAGE_SIZE = 50


def main(argv):
  # Check first to ensure a custom channel ID has been provided.
  if not custom_channel_id:
    raise ValueError(f'Missing argument: "custom_channel_id". Must be of format '
                     '"accounts/{account}/adclients/{adclient}/customchannels/'
                     '{customchannel}".')

  # Authenticate and construct service.
  credentials = adsense_util.get_adsense_credentials()
  with discovery.build('adsense', 'v2', credentials = credentials) as service:
    try:
      # Retrieve the ad units for the provided AdClient id.
      request = service.accounts().adclients().customchannels() \
          .listLinkedAdUnits(parent=custom_channel_id, pageSize=MAX_PAGE_SIZE)

      while request is not None:
        result = request.execute()

        if 'adUnits' in result:
          for ad_unit in result['adUnits']:
            print('Ad unit with ID "%s", name "%s", and state "%s" was found.'
                   % (ad_unit['name'], ad_unit['displayName'],
                      ad_unit['state']))
        else:
          print('No ad units were found.')

        request = service.accounts().list_next(request, result)

    except google.auth.exceptions.RefreshError:
      print('The credentials have been revoked or expired, please delete the '
            '"%s" file and re-run the application to re-authorize.' %
            adsense_util.CREDENTIALS_FILE)


if __name__ == '__main__':
  main(sys.argv)
