/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.util.Map;

public interface iInfoListener <T>{
  public void infoChanged(String aPeerId, Map<String, T> aInfoMap);
} 
