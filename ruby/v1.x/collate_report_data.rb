#!/usr/bin/env ruby
# Encoding: utf-8
#
# Author:: sgomes@google.com (Sérgio Gomes)
#
# Copyright:: Copyright 2014, Google Inc. All Rights Reserved.
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
# Retrieves two reports and collates data between them, taking into account any
# missing "categories". For example, if a platform is missing from one of the
# requests in this sample, metric values will be filled in automatically with
# zeroes.
#
# To get ad clients, run get_all_ad_clients.rb.
#
# Tags: accounts.reports.generate, accounts.reports.saved.generate

require_relative 'adsense_common'

def collate_report_data(adsense)
  account_id = choose_account(adsense)

  report_template = {
    :accountId => account_id,
    :metric => ['CLICKS', 'EARNINGS'],
    :dimension => ['PLATFORM_TYPE_CODE', 'PLATFORM_TYPE_NAME'],
    :sort => ['+PLATFORM_TYPE_CODE']
  }

  # Generate a report for "last week", i.e., the 7-day period ending yesterday.
  last = adsense.accounts.reports.generate(report_template.merge({
      :startDate => 'today-7d', :endDate => 'today-1d'
  })).execute

  # Generate a report for "previous week", i.e., the 7-day period ending eight
  # days ago.
  previous = adsense.accounts.reports.generate(report_template.merge({
      :startDate => 'today-14d', :endDate => 'today-8d'
  })).execute

  # Store data in hash maps, with the first column as the index and ignoring the
  # second column (the user-readable name).
  last_data = last.data.rows.reduce({}) do |hash,entry|
    hash[entry.first] = entry[2..-1]
    hash
  end
  previous_data = previous.data.rows.reduce({}) do |hash,entry|
    hash[entry.first] = entry[2..-1]
    hash
  end

  # Compile list of names for all platforms, with the code as the index.
  platform_names =
      (last.data.rows + previous.data.rows).reduce({}) do |hash,entry|
    hash[entry.first] = entry[1]
    hash
  end

  # Store list of all platforms across "last" and "previous".
  platforms = last_data.keys | previous_data.keys

  # Fill missing data in both datasets.
  datasets = [last_data, previous_data]
  datasets.each_with_index do |dataset,i|
    other_dataset = (i == 0 ? datasets[1] : datasets[0])

    (platforms - dataset.keys).each do |missing|
      # Copy the corresponding row in the other dataset, replacing metric values
      # with zeroes.
      dataset[missing] = Array.new(other_dataset[missing].length, '0')
    end
  end

  # Display effective date range.
  puts 'Results for last week (%s to %s) versus the previous week (%s to %s).' %
      [last.data.startDate, last.data.endDate,
       previous.data.startDate, previous.data.endDate]
  puts

  # Display results per platform.
  platforms.sort.each do |platform|
    puts '%s:' % platform_names[platform]
    last_data[platform].each_with_index do |metric_last,i|
      metric_previous = previous_data[platform][i]
      # Adding 2 to skip headers for dimensions.
      metric_name = last.data.headers[i + 2].name
      puts '- %s delta (%s last week vs %s in the previous week) on %s' %
          [
            metric_last.to_f - metric_previous.to_f,
            metric_last,
            metric_previous,
            metric_name
          ]
    end
  end
end


if __FILE__ == $0
  adsense = service_setup()
  collate_report_data(adsense)
end
