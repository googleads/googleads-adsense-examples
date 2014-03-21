/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.adsensequickstart.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that represents the inventory of an account.
 */
public class Inventory implements Serializable {

  private List<String> mAccounts = new ArrayList<String>();
  private final Map<String, List<String>> mAdClients = new HashMap<String, List<String>>();
  private final Map<String, List<String>> mAdUnits = new HashMap<String, List<String>>();
  private final Map<String, List<String>> mCustomChannels = new HashMap<String, List<String>>();

  public List<String> getAccounts() {
    return mAccounts;
  }

  public void setAccounts(List<String> mAccounts) {
    this.mAccounts = mAccounts;
  }

  public List<String> getAdClients(String accountId) {
    return mAdClients.get(accountId);
  }

  public void setAdClients(String accountId, List<String> mAdClients) {
    this.mAdClients.put(accountId, mAdClients);
  }

  public List<String> getAdUnits(String adClientId) {
    return mAdUnits.get(adClientId);
  }

  public void setAdUnits(String adClientId, List<String> adUnits) {
    mAdUnits.put(adClientId, adUnits);
  }

  public List<String> getCustomChannels(String adClientId) {
    return mCustomChannels.get(adClientId);
  }

  public void setCustomChannels(String adClientId, List<String> customChannels) {
    mCustomChannels.put(adClientId, customChannels);
  }
}
