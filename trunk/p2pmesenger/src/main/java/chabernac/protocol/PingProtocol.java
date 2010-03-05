/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class PingProtocol extends Protocol {
  
  private static enum Command{ PING };
  private static enum Response{ PONG, UNKNOWN_COMMAND };  
  

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
    if(Command.PING.name().equalsIgnoreCase( theCommand )){
      return Response.PONG.name();
    }
    
    return Response.UNKNOWN_COMMAND.name();
  }

}
