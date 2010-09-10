/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;


public class EndPoint {
  private final String myId;
  private String myData = null;
  
  public EndPoint ( String anId ) {
    super();
    myId = anId;
  }

  public String getId() {
    return myId;
  }

  public synchronized String getData() throws InterruptedException{
    while(myData == null){
      wait();
    }
    return myData;
  }
  
  public synchronized void setData(String aData){
    myData = aData;
    notifyAll();
  }
  
  
}
