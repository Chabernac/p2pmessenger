/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.io.File;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.P2PServerFactoryException;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolWebServer;
import chabernac.protocol.iP2PServer;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.ScanWebSystem;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.WebPeerProtocol;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserInfoProtocolTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testUserInfoProtocol() throws ProtocolException, UserInfoException, InterruptedException, SocketException, UnknownPeerException, NoAvailableNetworkAdapterException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
    UserInfoListener theListener = new UserInfoListener();
    theUserInfoProtocol2.addUserInfoListener( theListener );

    try{
      UserInfoProtocol theUserInfoProtocol = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep( 1000 );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep(SLEEP_AFTER_SCAN);

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );


      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();

      assertTrue(theRoutingTable1.containsEntryForPeer("2"));
      assertTrue(theRoutingTable2.containsEntryForPeer("1"));

      UserInfo theUserInfo = theUserInfoProtocol.getUserInfoForPeer( theRoutingProtocol1.getRoutingTable().getEntryForPeer( "2" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo );
      assertNotNull( theUserInfo.getId() );
      assertTrue( theUserInfo.getId().length() > 0 );

      Thread.sleep( 5000 );

      UserInfo theUserInfo2 = theUserInfoProtocol2.getUserInfo().get( theRoutingProtocol2.getRoutingTable().getEntryForPeer( "1" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo2 );
      assertNotNull( theUserInfo2.getId() );
      assertTrue( theUserInfo2.getId().length() > 0 );

      //now lets change the status of the user of peer 1 and see if its emmediately reflected on peer 2
      //first test the old status
      assertEquals( Status.ONLINE, theUserInfoProtocol2.getUserInfoForPeer( "1" ).getStatus() );

      theUserInfoProtocol.getPersonalInfo().setStatus( Status.BUSY );

      //it might take just a little while before the information has spread trough the network
      Thread.sleep( 500 );

      //now test the status of user of peer 1 on peer 2
      assertEquals( Status.BUSY, theUserInfoProtocol2.getUserInfo().get(  "1" ).getStatus() );

      //lets change the name of the user on peer 2
      theUserInfoProtocol2.getPersonalInfo().setName( "Chabernac" );
      Thread.sleep( 500 );
      assertEquals( "Chabernac", theUserInfoProtocol.getUserInfo().get( "2" ).getName() );

      Thread.sleep( 2000 );

      assertEquals( 5, theListener.getChangedUserInfo().size() );

      //1 event when the user changed its status to busy
      assertEquals( Status.BUSY, theListener.getChangedUserInfo().get( 3 ).getStatus());

      //1 event when the user name changed to chabernac
      assertEquals( Status.ONLINE, theListener.getChangedUserInfo().get( 4 ).getStatus());


    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  public void testUserInfoWithPersistingRoutingTable() throws InterruptedException, ProtocolException, UnknownPeerException, UserInfoException, P2PServerFactoryException{
    File theRoutingTable1File = new File("RoutingTable_1.bin");
    if(theRoutingTable1File.exists()) theRoutingTable1File.delete();
    File theRoutingTable2File = new File("RoutingTable_2.bin");
    if(theRoutingTable2File.exists()) theRoutingTable2File.delete();


    ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, true, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    try{
      UserInfoProtocol theUserInfoProtocol = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep( 1000 );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep(SLEEP_AFTER_SCAN);

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );


      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();

      assertNotNull( theUserInfoProtocol.getUserInfo().get( theRoutingTable2.getLocalPeerId() ) );

      theServer1.stop();
      theServer2.stop();

      assertTrue( theRoutingTable1File.exists() );
      assertTrue( theRoutingTable2File.exists() );

      theProtocol1 = getProtocolContainer( -1, true, "1" );
      theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

      theProtocol2 = getProtocolContainer( -1, true, "2" );
      theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

      theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
      theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep( 1000 );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep(1000);

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );

      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();

      assertNotNull( theUserInfoProtocol.getUserInfo().get( theRoutingTable2.getLocalPeerId() ) );
    }finally{
      theServer1.stop();
      theServer2.stop();
    }
  }

  public void testRemoveUserInfo() throws ProtocolException, UserInfoException, InterruptedException, SocketException, UnknownPeerException, NoAvailableNetworkAdapterException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
    UserInfoListener theListener = new UserInfoListener();
    theUserInfoProtocol2.addUserInfoListener( theListener );

    try{
      UserInfoProtocol theUserInfoProtocol = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep( SLEEP_AFTER_SCAN );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep(SLEEP_AFTER_SCAN);

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );


      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();

      UserInfo theUserInfo = theUserInfoProtocol.getUserInfoForPeer( theRoutingProtocol1.getRoutingTable().getEntryForPeer( "2" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo );
      assertNotNull( theUserInfo.getId() );
      assertTrue( theUserInfo.getId().length() > 0 );


      UserInfo theUserInfo2 = theUserInfoProtocol2.getUserInfo().get( theRoutingProtocol2.getRoutingTable().getEntryForPeer( "1" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo2 );
      assertNotNull( theUserInfo2.getId() );
      assertTrue( theUserInfo2.getId().length() > 0 );

      theServer2.stop();

      Thread.sleep( SLEEP_AFTER_SCAN );

      theUserInfo = theUserInfoProtocol.getUserInfo().get(theRoutingTable2.getLocalPeerId());

      assertNotNull( theUserInfo );
      assertNotNull( theUserInfo.getId() );
      assertTrue( theUserInfo.getId().length() > 0 );
      assertEquals( Status.OFFLINE, theUserInfo.getStatus() );

      RoutingTableEntry theEntryForPeer2 = theRoutingTable1.getEntryForPeer( theRoutingTable2.getLocalPeerId() );
      theRoutingTable1.removeRoutingTableEntry( theEntryForPeer2 );

      Thread.sleep( SLEEP_AFTER_SCAN );

      theUserInfo = theUserInfoProtocol.getUserInfo().get(theRoutingTable2.getLocalPeerId());

      assertNotNull( theUserInfo );
      assertEquals( Status.OFFLINE, theUserInfo.getStatus() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  public void testUserInfoProtocolThroughWebPeer() throws Exception{
    iP2PServer theServer1 = null;
    iP2PServer theServer2 = null;
    Server theWebServer = null;
    try{
      ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
      theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      UserInfoProtocol theUserInfoProtocol1 = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );
      theRoutingProtocol1.getLocalUnreachablePeerIds().add("2");
      theProtocol1.getProtocol( WebPeerProtocol.ID );

      ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
      theServer2 = getP2PServer(theProtocol2, RoutingProtocol.START_PORT + 1);
      RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
      UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
      theRoutingProtocol2.getLocalUnreachablePeerIds().add("1");
      theProtocol2.getProtocol( WebPeerProtocol.ID );

      theWebServer = new Server(9090);

      Context root = new Context(theWebServer,ProtocolWebServer.CONTEXT,Context.SESSIONS);
      CometServlet theCometServlet= new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder(1);
      root.addServlet(theCometHolder, ProtocolWebServer.COMET);
      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, ProtocolWebServer.PROTOCOL);
      theProtocolHolder.setInitParameter( "serverurl", "http://localhost:9090" + ProtocolWebServer.CONTEXT );

      theWebServer.start();

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      Thread.sleep( 1000 );

      assertTrue( theWebServer.isRunning() );

      new ScanWebSystem(theRoutingProtocol1, new URL("http://localhost:9090/")).run();
      new ScanWebSystem(theRoutingProtocol2, new URL("http://localhost:9090/")).run();

      Thread.sleep( 5000 );

      //after the webpeer has been added, entries must be present in the comet servlet endpoints
      assertTrue( theCometServlet.getEndPointContainer().containsEndPointFor( "1"));
      assertTrue( theCometServlet.getEndPointContainer().containsEndPointFor( "2"));

      RoutingProtocol theWebPeerRoutingProtocol = (RoutingProtocol)theProtocolServlet.getProtocolContainer().getProtocol(RoutingProtocol.ID);
      String theWebPeerId = theWebPeerRoutingProtocol.getRoutingTable().getLocalPeerId();
      theWebPeerRoutingProtocol.getRoutingTable().setKeepHistory(true);


      for(int i=0;i<3;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
      }

      Thread.sleep( 1000 );

      assertTrue(theRoutingProtocol1.getRoutingTable().containsEntryForPeer(theWebPeerId));
      assertEquals(1, theRoutingProtocol1.getRoutingTable().getEntryForPeer(theWebPeerId).getHopDistance());
      assertEquals(2, theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getHopDistance());
      assertEquals(theWebPeerId, theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getGateway().getPeerId());
      
      UserInfo theUserInfo = theUserInfoProtocol1.getUserInfoForPeer( theRoutingProtocol1.getRoutingTable().getEntryForPeer( "2" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo );
      assertNotNull( theUserInfo.getId() );
      assertTrue( theUserInfo.getId().length() > 0 );


      UserInfo theUserInfo2 = theUserInfoProtocol2.getUserInfo().get( theRoutingProtocol2.getRoutingTable().getEntryForPeer( "1" ).getPeer().getPeerId() );

      assertNotNull( theUserInfo2 );
      assertNotNull( theUserInfo2.getId() );
      assertTrue( theUserInfo2.getId().length() > 0 );
    }finally{
      if(theServer1 != null) theServer1.stop();
      if(theServer2 != null) theServer2.stop();
      if(theWebServer != null) theWebServer.stop();
    }
  }


  public void testPeerGoesOffline() throws ProtocolException, InterruptedException, P2PServerFactoryException, UnknownPeerException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );

    UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    UserInfoListener theListener = new UserInfoListener();
    theUserInfoProtocol2.addUserInfoListener( theListener );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep( 1000 );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep(SLEEP_AFTER_SCAN);

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer(5) );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer(5) );

      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();

      theRoutingTable2.getEntryForPeer( theRoutingTable1.getLocalPeerId(), 5 );
      theRoutingTable1.getEntryForPeer( theRoutingTable2.getLocalPeerId(), 5 );
      
      UserInfo theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( Status.ONLINE, theUserInfo.getStatus() );

      theServer1.stop();

      Thread.sleep( SLEEP_AFTER_SCAN );

      theRoutingProtocol2.exchangeRoutingTable();

      Thread.sleep( SLEEP_AFTER_SCAN );

      theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( Status.OFFLINE, theUserInfo.getStatus() );

      theServer1.start();

      Thread.sleep( SLEEP_AFTER_SCAN );

      theRoutingProtocol2.exchangeRoutingTable();

      Thread.sleep(SLEEP_AFTER_SCAN);
      theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( Status.ONLINE, theUserInfo.getStatus() );

    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }


  private class UserInfoListener implements iUserInfoListener{
    private List< UserInfo > myChangedUserInfo = new ArrayList< UserInfo >();

    @Override
    public void userInfoChanged( UserInfo aUserInfo, Map< String, UserInfo > aFullUserInfoList ) {
      myChangedUserInfo.add(aUserInfo);
    }

    public List<UserInfo> getChangedUserInfo(){
      return myChangedUserInfo;
    }
  }

  public void testChangeStatusRemotely() throws ProtocolException, UserInfoException, InterruptedException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );

    try{
      UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
      UserInfoProtocol theUserInfoProtocol1 = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );

      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep(SLEEP_AFTER_SCAN);

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      Thread.sleep( SLEEP_AFTER_SCAN );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.AWAY );
      Thread.sleep( 1000 );
      assertEquals( Status.AWAY, theUserInfoProtocol2.getPersonalInfo().getStatus() );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.BUSY );
      Thread.sleep( 1000 );
      assertEquals( Status.BUSY, theUserInfoProtocol2.getPersonalInfo().getStatus() );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.ONLINE );
      Thread.sleep( 1000 );
      assertEquals( Status.ONLINE, theUserInfoProtocol2.getPersonalInfo().getStatus() );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.AWAY, "1" );
      Thread.sleep( 1000 );
      assertEquals( Status.AWAY, theUserInfoProtocol2.getPersonalInfo().getStatus() );
      assertEquals( "1",  theUserInfoProtocol2.getPersonalInfo().getStatusMessage() );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.BUSY, "2" );
      Thread.sleep( 1000 );
      assertEquals( Status.BUSY, theUserInfoProtocol2.getPersonalInfo().getStatus() );
      assertEquals( "2",  theUserInfoProtocol2.getPersonalInfo().getStatusMessage() );

      theUserInfoProtocol1.changeStatus( theUserInfoProtocol2.getPersonalInfo().getId(), Status.ONLINE, "3" );
      Thread.sleep( 1000 );
      assertEquals( Status.ONLINE, theUserInfoProtocol2.getPersonalInfo().getStatus() );
      assertEquals( "3",  theUserInfoProtocol2.getPersonalInfo().getStatusMessage() );

    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
}
