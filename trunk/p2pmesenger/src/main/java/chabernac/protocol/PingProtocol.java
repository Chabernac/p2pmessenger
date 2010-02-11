/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class PingProtocol extends Protocol {

  public PingProtocol (  ) {
    super( "PIN" );
  }

  @Override
  public String getDescription() {
    return "Ping protocol";
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
    String theCommand = new String(anInput);
    if("ping".equalsIgnoreCase( theCommand )){
      return "pong";
    }
    
    return "unknwown command";
  }

}
