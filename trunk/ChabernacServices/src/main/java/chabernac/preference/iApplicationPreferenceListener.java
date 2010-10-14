/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.preference;

public interface iApplicationPreferenceListener {
  public void applicationPreferenceChanged(String aKey, String aValue);
  public void applicationPreferenceChanged(Enum anEnumValue);
}
