/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.Map;

public interface iDataHandler {
  public void handleData(String aData, Map< String, EndPoint > anEndPoints) throws DataHandlingException;
}
