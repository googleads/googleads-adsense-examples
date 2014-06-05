#!/usr/bin/env ruby
# Encoding: utf-8
#
# Author:: sgomes@google.com (Sérgio Gomes)
#
# Copyright:: Copyright 2013, Google Inc. All Rights Reserved.
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
# Handles common tasks across all AdSense Management API samples.

require 'date'
require 'google/api_client'
require 'google/api_client/service'
require 'google/api_client/client_secrets'
require 'google/api_client/auth/file_storage'
require 'google/api_client/auth/installed_app'

API_NAME = 'adsense'
API_VERSION = 'v1.4'
API_SCOPE = 'https://www.googleapis.com/auth/adsense.readonly'
CREDENTIAL_STORE_FILE = "#{API_NAME}-oauth2.json"

# Handles authentication and loading of the API.
def service_setup()
  # Uncomment the following lines to enable logging.
  #log_file = File.open("#{$0}.log", 'a+')
  #log_file.sync = true
  #logger = Logger.new(log_file)
  #logger.level = Logger::DEBUG
  #Google::APIClient.logger = logger # Logging is set globally

  authorization = nil
  # FileStorage stores auth credentials in a file, so they survive multiple runs
  # of the application. This avoids prompting the user for authorization every
  # time the access token expires, by remembering the refresh token.
  #
  # Note: FileStorage is not suitable for multi-user applications.
  file_storage = Google::APIClient::FileStorage.new(CREDENTIAL_STORE_FILE)
  if file_storage.authorization.nil?
    client_secrets = Google::APIClient::ClientSecrets.load
    # The InstalledAppFlow is a helper class to handle the OAuth 2.0 installed
    # application flow, which ties in with FileStorage to store credentials
    # between runs.
    flow = Google::APIClient::InstalledAppFlow.new(
      :client_id => client_secrets.client_id,
      :client_secret => client_secrets.client_secret,
      :scope => [API_SCOPE]
    )
    authorization = flow.authorize(file_storage)
  else
    authorization = file_storage.authorization
  end

  # Initialize API Service.
  #
  # Note: the client library automatically creates a cache file for discovery
  # documents, to avoid calling the discovery service on every invocation.
  # To set this to an ActiveSupport cache store, use the :cache_store parameter
  # (or, alternatively, set it to nil if you want to disable caching).
  service = Google::APIClient::Service.new(API_NAME, API_VERSION,
    {
      :application_name => "Ruby #{API_NAME} samples: #{$0}",
      :application_version => '1.0.0',
      :authorization => authorization
    }
  )

  return service
end

# Lists all AdSense accounts the user has access to, and prompts them to choose
# one. Returns the account ID.
def choose_account(adsense)
  result = adsense.accounts.list().execute()
  account = nil

  if !result || !result.data || result.data.items.empty?
    puts 'No AdSense accounts found. Exiting.'
    exit
  elsif result.data.items.length == 1
    account = result.data.items.first
    puts 'Only one account found (%s), using it.' % account.id
  else
    puts 'Please choose one of the following options: '
    result.data.items.each_with_index do |acc,i|
      puts '%d. %s (%s)' % [i + 1, acc.name, acc.id]
    end
    print '> '
    account_index = Integer(gets.chomp) - 1
    account = result.data.items[account_index]
    puts 'Account %s chosen, resuming.' % account.id
  end

  return account.id
end

# Fills in missing date ranges from a report. This is needed because null
# data for a given period causes that reporting row to be ommitted, rather than
# set to zero (or the appropriate missing value for the metric in question).
#
# NOTE: This code assumes you have a single dimension in your report, and that
# the dimension is either DATE or MONTH. The number of metrics is not relevant.
#
def fill_missing_dates(report_result)
  start_date = Date.parse(report_result.data.startDate)
  end_date = Date.parse(report_result.data.endDate)
  headers = report_result.data.headers
  rows = report_result.data.rows
  current_pos = 0

  # Check if the results fit the requirements for this method.
  if !headers
    raise ArgumentError, 'No headers defined in report results.'
  end

  if headers.length < 2 || headers.first.type != 'DIMENSION'
    raise ArgumentError, 'Insufficient dimensions and metrics defined.'
  end

  if headers[1].type == 'DIMENSION'
    raise ArgumentError, 'Only one dimension allowed.'
  end

  date_format, date = nil

  # Adjust output format and start date according to time period.
  if headers.first.name == 'DATE'
    date_format = '%F'
    date = start_date
  elsif headers.first.name == 'MONTH'
    date_format = '%Y-%m'
    # Normalize date to 1st of the month.
    date = Date.parse(start_date.strftime(date_format) + '-01')
  else
    raise ArgumentError, 'Results require a DATE or MONTH dimension.'
  end

  # Process data.
  processed_data = []

  while date < end_date do
    row_date = nil
    if rows && rows[current_pos]
      # Parse date on current row.
      if headers.first.name == 'DATE'
        row_date = Date.parse(rows[current_pos][0])
      elsif headers.first.name == 'MONTH'
        row_date = Date.parse(rows[current_pos][0] + '-01')
      end
    end

    # Is there an entry for this date?
    if row_date && date == row_date
      processed_data << rows[current_pos]
      current_pos += 1
    else
      new_row = []
      new_row << date.strftime(date_format)
      new_row += headers[1..-1].map { |header| 'no data' }
      processed_data << new_row
    end

    # Increment date accordingly.
    if headers.first.name == 'DATE'
      date += 1
    elsif headers.first.name == 'MONTH'
      date >>= 1
    end
  end

  return processed_data
end
