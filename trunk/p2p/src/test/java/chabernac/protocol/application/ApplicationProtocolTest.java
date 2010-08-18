/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.iProtocolDelegate;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class ApplicationProtocolTest extends AbstractProtocolTest {
  public void testApplicationProtocol() throws P2PFacadeException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( -1 )
    .setPersist( false )
    .start( 5 );
    
    ApplicationProtocolDelegate theDelegate = new ApplicationProtocolDelegate();
    theFacade1.setApplicationProtocolDelegate( theDelegate  );

    int theTimes = 50;
    for(int i=0;i<theTimes;i++){
      theFacade1.sendApplicationMessage( theFacade1.getPeerId(), "test " + i );
    }
    
    assertEquals( theTimes, theDelegate.getInput().size());
  }
  
  private class ApplicationProtocolDelegate implements iProtocolDelegate{
    private List< String > myInput = new ArrayList< String >();

    @Override
    public String handleCommand( long aSessionId, String anInput ) {
      myInput.add( anInput );
      return "OK";
    }
    
    public List<String> getInput(){
      return Collections.unmodifiableList( myInput );
    }
    
  }
}
