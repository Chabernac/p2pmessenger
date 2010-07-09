/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.version;

import junit.framework.TestCase;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.version.Version;

public class VersionProtocolTest extends TestCase {
  public void testVersions() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setPeerId( "1" )
    .setExchangeDelay( 3 )
    .setPersist( false )
    .setVersion(new Version("1.0.0"))
    .start( 5 );
    
    P2PFacade theFacade2 = new P2PFacade()
    .setPeerId( "2" )
    .setExchangeDelay( 3 )
    .setPersist( false )
    .setVersion(new Version("2.0.0"))
    .start( 5 );
    
    Thread.sleep( 2000 );
    
    assertEquals( 2, theFacade1.getVersions().size() );
    assertTrue( theFacade1.getVersions().containsKey( theFacade2.getPeerId() ) );
    assertEquals( theFacade2.getLocalVersion(), theFacade1.getVersions().get( theFacade2.getPeerId() ));
    
    
    assertEquals( 2, theFacade2.getVersions().size() );
    assertTrue( theFacade2.getVersions().containsKey( theFacade1.getPeerId() ) );
    assertEquals( theFacade1.getLocalVersion(), theFacade2.getVersions().get( theFacade1.getPeerId() ));

    
    
  }
}
