/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class MasterProtocol extends Protocol {

  public MasterProtocol ( ) {
    super( "MAS" );
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
    String theCommand = new String(anInput);
    StringBuffer theReply = new StringBuffer();
    if("protocols".equalsIgnoreCase( theCommand )){
      for(Protocol theProtocol : mySubProtocols.values()){
        theReply.append( theProtocol.getId() );
        theReply.append( ":" );
        theReply.append( theProtocol.getDescription() );
        theReply.append( "|" );
      }
    }
    return theReply.toString();
  }

  @Override
  public String getDescription() {
    return "Master protocol";
  }

}
