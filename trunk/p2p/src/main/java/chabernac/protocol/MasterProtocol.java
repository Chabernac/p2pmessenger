/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

/**
 * @deprecated
 */

public class MasterProtocol extends Protocol {
  public static enum Command{PROTOCOLS};
  public static enum Result{UNKNOWN_COMMAND};
  

  public MasterProtocol ( ) {
    super( "MAS" );
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    String theCommand = new String(anInput);
    if(Command.PROTOCOLS.name().equalsIgnoreCase( theCommand )){
//      return getProtocolsString();
    }
    return Result.UNKNOWN_COMMAND.name();
  }

  @Override
  public String getDescription() {
    return "Master protocol";
  }

  @Override
  public void stop() {
  }



}
