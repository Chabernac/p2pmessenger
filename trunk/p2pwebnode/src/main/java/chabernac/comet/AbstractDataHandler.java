/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.Map;


public abstract class AbstractDataHandler {
  protected Map<String, EndPoint> myEndPoints;
  

  public Map<String, EndPoint> getEndPoints() {
    return myEndPoints;
  }
  
  public void setEndPoints(Map<String, EndPoint> anEndPoints) {
    myEndPoints = anEndPoints;
  }

  public abstract String handleData(String aData) throws DataHandlingException;
}
