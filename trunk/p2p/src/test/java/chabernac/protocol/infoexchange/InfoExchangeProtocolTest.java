/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.pominfoexchange.POMInfo;
import chabernac.protocol.version.Version;

public class InfoExchangeProtocolTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testInfoProtocol() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 3 )
    .setPersist( false )
    .start( 5 );

    InfoCollecter theInfoCollecter1 = new InfoCollecter();
    theFacade1.addInfoListener( theInfoCollecter1 );

    theFacade1.getInfoObject().put( "version", new Version("1.0.0") );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 3 )
    .setPersist( false )
    .start( 5 );

    try{
      InfoCollecter theInfoCollecter2 = new InfoCollecter();
      theFacade2.addInfoListener( theInfoCollecter2 );

      theFacade2.getInfoObject().put( "version", new Version("2.0.0") );

      Thread.sleep( 2000 );

      assertNotNull( theInfoCollecter1.getInfo() );
      assertNotNull( theInfoCollecter2.getInfo() );

      assertTrue( theInfoCollecter1.getInfo().containsKey( theFacade2.getPeerId() ) );
      assertTrue( theInfoCollecter2.getInfo().containsKey( theFacade1.getPeerId() ) );

      InfoObject theInfoObjectOfPeer2inPeer1 = theInfoCollecter1.getInfo().get( theFacade2.getPeerId() );
      assertEquals( "2.0.0", theInfoObjectOfPeer2inPeer1.get( "version" ).toString());

      InfoObject theInfoObjectOfPeer1inPeer2 = theInfoCollecter2.getInfo().get( theFacade1.getPeerId() );
      assertEquals( "1.0.0", theInfoObjectOfPeer1inPeer2.get( "version" ).toString());
    }finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  public void testInfoProtocolWithPomInfo() throws P2PFacadeException, IOException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 3 )
    .setPersist( false )
    .start( 5 );

    InfoCollecter theInfoCollecter1 = new InfoCollecter();
    theFacade1.addInfoListener( theInfoCollecter1 );

    theFacade1.getInfoObject().put( "pom.info", new POMInfo());

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 3 )
    .setPersist( false )
    .start( 5 );

    try{
      InfoCollecter theInfoCollecter2 = new InfoCollecter();
      theFacade2.addInfoListener( theInfoCollecter2 );

      theFacade2.getInfoObject().put( "pom.info", new POMInfo());

      Thread.sleep( 2000 );

      assertNotNull( theInfoCollecter1.getInfo() );
      assertNotNull( theInfoCollecter2.getInfo() );

      assertTrue( theInfoCollecter1.getInfo().containsKey( theFacade2.getPeerId() ) );
      assertTrue( theInfoCollecter2.getInfo().containsKey( theFacade1.getPeerId() ) );

      InfoObject theInfoObjectOfPeer2inPeer1 = theInfoCollecter1.getInfo().get( theFacade2.getPeerId() );
      assertTrue( theInfoObjectOfPeer2inPeer1.get( "pom.info" ) instanceof POMInfo);

      InfoObject theInfoObjectOfPeer1inPeer2 = theInfoCollecter2.getInfo().get( theFacade1.getPeerId() );
      assertTrue( theInfoObjectOfPeer1inPeer2.get( "pom.info" ) instanceof POMInfo);
    }finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  private class InfoCollecter implements iInfoListener< InfoObject >{
    private Map< String, InfoObject > myInfo;

    @Override
    public void infoChanged( String aPeerId, Map< String, InfoObject > aInfoMap ) {
      myInfo = aInfoMap;
    }

    public Map< String, InfoObject > getInfo(){
      return myInfo;
    }

  }
}
