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
# Gets all alerts available for the logged in user's default account.
#
# Tags: list_account_alerts

require_relative 'adsense_common'

def get_all_alerts(adsense)
  account_id = choose_account(adsense)

  result = adsense.list_account_alerts(account_id)

  if result && result.alerts && !result.alerts.empty?
    result.alerts.each do |alert|
      puts 'Alert ID "%s" with severity "%s" and type "%s" was found.'  %
        [alert.name, alert.severity, alert.type]
    end
  end
end


if __FILE__ == $0
  adsense = service_setup()
  get_all_alerts(adsense)
end
