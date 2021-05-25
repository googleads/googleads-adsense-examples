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
# This example gets all custom channels an ad unit has been added to.
#
# To get ad units, run get_all_ad_units.rb.
#
# Tags: list_account_adclient_adunit_linked_custom_channels

require_relative 'adsense_common'
require 'optparse'

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50

def get_all_custom_channels_for_ad_unit(adsense, options)
  page_token = nil
  loop do
    result = adsense.list_account_adclient_adunit_linked_custom_channels(
        options[:ad_unit_id], :page_size => MAX_PAGE_SIZE,
        :page_token => page_token)

    if result && result.custom_channels && !result.custom_channels.empty?
      result.custom_channels.each do |custom_channel|
        puts 'Custom channel with ID "%s" and name "%s" was found.' %
            [custom_channel.name, custom_channel.display_name]
       end
    else
      puts 'No custom channels were found.'
    end

    break unless result.next_page_token
    page_token = result.next_page_token
  end
end


if __FILE__ == $0
  adsense = service_setup()

  options = {}

  optparse = OptionParser.new do |opts|
    opts.on('-u', '--ad_unit_id AD_UNIT_ID',
            'The ID of the ad unit to get linked custom channels for') do |id|
      options[:ad_unit_id] = id
    end
  end

  begin
    optparse.parse!
    if options[:ad_unit_id].nil?
      raise OptionParser::MissingArgument
    end
  rescue OptionParser::MissingArgument
    puts 'Please specify an ad_unit_id.'
    puts optparse
    exit
  rescue OptionParser::InvalidOption
    puts optparse
    exit
  end

  get_all_custom_channels_for_ad_unit(adsense, options)
end
