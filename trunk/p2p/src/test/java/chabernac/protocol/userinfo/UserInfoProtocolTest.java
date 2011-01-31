/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserInfoProtocolTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testUserInfoProtocol() throws ProtocolException, UserInfoException, InterruptedException, SocketException, UnknownPeerException, NoAvailableNetworkAdapterException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
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
  
  public void testUserInfoWithPersistingRoutingTable() throws InterruptedException, ProtocolException, UnknownPeerException, UserInfoException{
    File theRoutingTable1File = new File("RoutingTable_1.bin");
    if(theRoutingTable1File.exists()) theRoutingTable1File.delete();
    File theRoutingTable2File = new File("RoutingTable_2.bin");
    if(theRoutingTable2File.exists()) theRoutingTable2File.delete();
    
    
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, true, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
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
      theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

      theProtocol2 = getProtocolContainer( -1, true, "2" );
      theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
      
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
  
  public void testRemoveUserInfo() throws ProtocolException, UserInfoException, InterruptedException, SocketException, UnknownPeerException, NoAvailableNetworkAdapterException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
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
      assertEquals( UserInfo.Status.OFFLINE, theUserInfo.getStatus() );
      
      RoutingTableEntry theEntryForPeer2 = theRoutingTable1.getEntryForPeer( theRoutingTable2.getLocalPeerId() );
      theRoutingTable1.removeRoutingTableEntry( theEntryForPeer2 );
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theUserInfo = theUserInfoProtocol.getUserInfo().get(theRoutingTable2.getLocalPeerId());
      
      assertNotNull( theUserInfo );
      assertEquals( UserInfo.Status.OFFLINE, theUserInfo.getStatus() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testPeerGoesOffline() throws ProtocolException, InterruptedException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
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
      
      UserInfo theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( UserInfo.Status.ONLINE, theUserInfo.getStatus() );
      
      theServer1.kill();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theRoutingProtocol2.exchangeRoutingTable();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( UserInfo.Status.OFFLINE, theUserInfo.getStatus() );
      
      theServer1.start();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theRoutingProtocol2.exchangeRoutingTable();
      
      Thread.sleep(SLEEP_AFTER_SCAN);
      theUserInfo = theUserInfoProtocol2.getUserInfo().get( theRoutingTable1.getLocalPeerId() ); 
      assertNotNull( theUserInfo );
      assertEquals( UserInfo.Status.ONLINE, theUserInfo.getStatus() );
      
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
  
  public void testChangeStatusRemotely() throws ProtocolException, UserInfoException, InterruptedException{
      ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
      ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);
      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );

      ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
      ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
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

       } finally {
        theServer1.stop();
        theServer2.stop();
      }
  }
}
