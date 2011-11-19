/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import chabernac.protocol.Protocol;

public class BlockingProtocol extends Protocol {
  private final long myTimeout;
  public static final String ID = "BLP";
  
  public BlockingProtocol( long aTimeout ) {
    super( ID );
    myTimeout = aTimeout;
  }

  @Override
  public String getDescription() {
    return "Blocking protocol";
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    try {
      Thread.sleep( myTimeout );
    } catch ( InterruptedException e ) {
    }
    return "OK";
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
