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
# Retrieves a saved report or a report for the specified ad client.
#
# To get ad clients, run get_all_ad_clients.rb.
#
# Tags: generate_account_report, generate_account_report_saved

require_relative 'adsense_common'
require 'optparse'

def generate_report(adsense, options)
  saved_report_id = options[:report_id]

  result = nil
  if saved_report_id
    # Generate a report from a saved report ID.
    result = adsense.generate_account_report_saved(saved_report_id,
                                                   :date_range => 'LAST_7_DAYS')
  else
    # Get the last part of the ad_client_id for filtering the report, and
    # either get the account_id from the full ad_client_id, or prompt the user
    # if only the last part of the ad_client_id was provided.
    ad_client_id_components = options[:ad_client_id].split('/')
    ad_client_id = ad_client_id_components[-1]
    if ad_client_id_components.size == 4
      account_id = 'accounts/' + ad_client_id_components[1]
    else
      account_id = choose_account(adsense)
    end

    # Generate a new report for the provided ad client ID.
    result = adsense.generate_account_report(
        account_id, :date_range => 'CUSTOM',
        :start_date_year => 2021, :start_date_month => 3, :start_date_day => 1,
        :end_date_year => 2021, :end_date_month => 3, :end_date_day => 31,
        :metrics => ['PAGE_VIEWS', 'AD_REQUESTS', 'AD_REQUESTS_COVERAGE',
                     'CLICKS', 'AD_REQUESTS_CTR', 'COST_PER_CLICK',
                     'AD_REQUESTS_RPM', 'ESTIMATED_EARNINGS'],
        :dimensions => ['DATE'],
        :filters => ['AD_CLIENT_ID==' + ad_client_id],
        :order_by => ['+DATE']
    )
  end

  # Display headers.
  result.headers.each do |header|
    print '%25s' % header.name
  end
  puts

  # Display results.
  if result && result.rows
    result.rows.each do |row|
      row.cells.each do |cell|
        print '%25s' % cell.value
      end
      puts
    end
  end

  # Display effective date range.
  puts 'Report from %s to %s.' % [date_to_iso_string(result.start_date),
                                  date_to_iso_string(result.end_date)]
end


if __FILE__ == $0
  adsense = service_setup()

  options = {}

  optparse = OptionParser.new do |opts|
    opts.on('-c', '--ad_client_id AD_CLIENT_ID',
            'The ID of the ad client for which to generate a report') do |id|
      options[:ad_client_id] = id
    end

    opts.on('-r', '--report_id REPORT_ID',
            'The ID of the saved report to generate') do |id|
      options[:report_id] = id
    end
  end

  begin
    optparse.parse!
    unless options[:ad_client_id].nil? ^ options[:report_id].nil?
      raise OptionParser::MissingArgument
    end
  rescue OptionParser::MissingArgument
    puts 'Please specify either ad_client_id or report_id.'
    puts optparse
    exit
  rescue OptionParser::InvalidOption
    puts optparse
    exit
  end

  generate_report(adsense, options)
end
