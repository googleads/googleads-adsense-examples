#!/usr/bin/env ruby
# Encoding: utf-8
#
# Copyright:: Copyright 2021, Google Inc. All Rights Reserved.
#
# License:: Licensed under the Apache License, Version 2.0 (the "License");
#           you may not use this file except in compliance with the License.
#           You may obtain a copy of the License at
#
#           http://www.apache.org/licenses/LICENSE-2.0
#
#           Unless required by applicable law or agreed to in writing, software
#           distributed under the License is distributed on an "AS IS" BASIS,
#           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
#           implied.
#           See the License for the specific language governing permissions and
#           limitations under the License.
#
# This example gets all ad clients for the selected account.
#
# Tags: list_account_adclients

require_relative 'adsense_common'

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50

def get_all_ad_clients(adsense)
  account_id = choose_account(adsense)

  page_token = nil
  loop do
    result = adsense.list_account_adclients(account_id,
                                           :page_size => MAX_PAGE_SIZE,
                                           :page_token => page_token)

    if result && result.ad_clients && !result.ad_clients.empty?
      result.ad_clients.each do |ad_client|
        puts 'Ad client for product "%s" with ID "%s" was found.' %
          [ad_client.product_code || 'N/A', ad_client.name]

        puts '  Supports reporting: %s' %
          (!ad_client.reporting_dimension_id.nil? ? 'Yes' : 'No')
      end
    else
      puts 'No ad clients were found.'
    end

    break unless result.next_page_token
    page_token = result.next_page_token
  end
end


if __FILE__ == $0
  adsense = service_setup()
  get_all_ad_clients(adsense)
end
