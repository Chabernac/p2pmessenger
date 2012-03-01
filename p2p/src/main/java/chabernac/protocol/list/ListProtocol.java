/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.list;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.tools.PropertyMap;

public class ListProtocol extends Protocol {
  public static final String ID = "LTP";
  
  public static enum Command { PROTOCOLS }; 

  public ListProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "List Protocol";
  }

  @Override
  public String handleCommand( String aSessionId, PropertyMap aProperties, String anInput ) {
    if(Command.PROTOCOLS.name().equalsIgnoreCase( anInput.trim() )){
      return findProtocolContainer().getProtocolString();
    }
    
    return ProtocolContainer.Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {

  }

}
