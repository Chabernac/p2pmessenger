/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.xmessage;

import chabernac.protocol.Protocol;

public class XMessageProtocol extends Protocol {

  public XMessageProtocol ( ) {
    super( "XMS" );
  }

  @Override
  public String getDescription() {
    return "X Message Protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
