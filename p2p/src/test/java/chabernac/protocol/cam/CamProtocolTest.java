/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2011.10.0 9-sep-2011 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2011.10.0      9-sep-2011
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class CamProtocolTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testCapture() throws Exception{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    CamProtocol theCamProtocol1 = (CamProtocol)theProtocol1.getProtocol( CamProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      assertTrue( theRoutingTable1.containsEntryForPeer( "2" ) );
      assertTrue( theRoutingTable2.containsEntryForPeer( "1" ) );
      
      CountDownLatch theLatch = new CountDownLatch( 1 );
      CamListener theCamListener = new CamListener(theLatch);
      theCamProtocol1.addCamListener( theCamListener );
      theCamProtocol1.requestCapture( "2", 100,100,0.5f );
      
      theLatch.await( 2000, TimeUnit.SECONDS );
      assertNotNull( theCamListener.getImage() );
    }catch(CamProtocolException e){
      if(e.getReason() != CamProtocolException.Reason.NO_CAPTURE_DEVICE) throw e;
    }  finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  private class CamListener implements iCamListener{
    private BufferedImage myImage = null;
    private final CountDownLatch myLatch;
    
    public CamListener(CountDownLatch aLatch){
      myLatch = aLatch;
    }
    @Override
    public void imageReceived( BufferedImage anImage ) {
      myImage = anImage;
      myLatch.countDown();
    }
    
    public BufferedImage getImage(){
      return myImage;
    }
  }

}
