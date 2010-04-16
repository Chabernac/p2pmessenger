/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.net.SocketException;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserInfoProtocolTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testUserInfoProtocol() throws ProtocolException, UserInfoException, InterruptedException, SocketException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      
      Thread.sleep(1000);
      
      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );

      
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      
      UserInfoProtocol theUserInfoProtocol = (UserInfoProtocol)theProtocol1.getProtocol( UserInfoProtocol.ID );
      
      UserInfo theUserInfo = theUserInfoProtocol.getUserInfoForPeer( theRoutingProtocol1.getRoutingTable().getEntryForPeer( "2" ).getPeer().getPeerId() );
      
      assertNotNull( theUserInfo );
      assertNotNull( theUserInfo.getId() );
      assertTrue( theUserInfo.getId().length() > 0 );
      
      
      UserInfoProtocol theUserInfoProtocol2 = (UserInfoProtocol)theProtocol2.getProtocol( UserInfoProtocol.ID );
      
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
      assertEquals( Status.BUSY, theUserInfoProtocol2.getUserInfoForPeer( "1" ).getStatus() );
      
      //lets change the name of the user on peer 2
      theUserInfoProtocol2.getPersonalInfo().setName( "Chabernac" );
      Thread.sleep( 500 );
      assertEquals( "Chabernac", theUserInfoProtocol.getUserInfoForPeer( "2" ).getName() );
      
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
}
