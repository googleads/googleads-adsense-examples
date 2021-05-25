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
# This example gets all the saved reports for the selected account.
#
# Tags: list_account_report_saveds

require_relative 'adsense_common'

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50

def get_all_saved_reports(adsense)
  account_id = choose_account(adsense)

  page_token = nil
  loop do
    result = adsense.list_account_report_saveds(account_id,
                                                :page_size => MAX_PAGE_SIZE,
                                                :page_token => page_token)

    if result && result.saved_reports && !result.saved_reports.empty?
      result.saved_reports.each do |saved_report|
        puts 'Saved report with ID "%s" and title "%s" was found.' %
          [saved_report.name, saved_report.title]
      end
    else
      puts 'No saved reports were found.'
    end

    break unless result.next_page_token
    page_token = result.next_page_token
  end
end


if __FILE__ == $0
  adsense = service_setup()
  get_all_saved_reports(adsense)
end
