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
# This example gets a specific account for the logged in user.
# This includes the full tree of sub-accounts.
#
# Tags: get_account, list_account_child_accounts

require_relative 'adsense_common'

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50

def get_account_tree(adsense)
  account_id = choose_account(adsense)
  account = adsense.get_account(account_id)
  display_tree(adsense, account) if account
end

def display_tree(adsense, account, level = 0)
  puts ('  ' * level) + ('Account with ID "%s" and name "%s" was found. ' %
    [account.name, account.display_name])

  response = adsense.list_account_child_accounts(account.name)

  if response && response.accounts
    response.accounts.each do |child_account|
      display_tree(adsense, child_account, level + 1)
    end
  end
end


if __FILE__ == $0
  adsense = service_setup()
  get_account_tree(adsense)
end
