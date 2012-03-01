/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.ping;

import chabernac.protocol.Protocol;

public class PingProtocol extends Protocol {
  public static final String ID = "PPG";  
  
  public static enum Command{ PING };
  public static enum Response{ PONG, UNKNOWN_COMMAND }

  public PingProtocol (  ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Ping protocol";
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    String theCommand = new String(anInput);
    if(Command.PING.name().equalsIgnoreCase( theCommand )){
      return Response.PONG.name();
    }
    
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
  }

}
