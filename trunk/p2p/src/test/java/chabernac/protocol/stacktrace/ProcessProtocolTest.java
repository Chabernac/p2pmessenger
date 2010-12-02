/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.stacktrace;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.UnknownPeerException;

public class ProcessProtocolTest extends AbstractProtocolTest  {
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testStackTraceProtocol() throws ProtocolException, UnknownPeerException, MessageException{
    ProtocolContainer theProtocolContainer = getProtocolContainer( -1, false, "1" );
    
    ProtocolServer theServer = new ProtocolServer(theProtocolContainer, RoutingProtocol.START_PORT, 5);
    
    RoutingProtocol theRoutingProtocol = (RoutingProtocol)theProtocolContainer.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol = (MessageProtocol)theProtocolContainer.getProtocol( MessageProtocol.ID );
    try{
      
      
      assertTrue( theServer.start() );
      
      Message theMessage = new Message();
      theMessage.setDestination( theRoutingProtocol.getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( "PRCFULL_STACK_TRACE" );
      theMessage.setProtocolMessage( true );
      String theStack = theMessageProtocol.sendMessage( theMessage );
      assertTrue( theStack.contains( "Full thread dump Java HotSpot" ) );
    }finally{
      theServer.stop();
    }
  }
  
  public void testProcess() throws ProtocolException, UnknownPeerException, MessageException{
    ProtocolContainer theProtocolContainer = getProtocolContainer( -1, false, "1" );
    
    ProtocolServer theServer = new ProtocolServer(theProtocolContainer, RoutingProtocol.START_PORT, 5);
    
    RoutingProtocol theRoutingProtocol = (RoutingProtocol)theProtocolContainer.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol = (MessageProtocol)theProtocolContainer.getProtocol( MessageProtocol.ID );
    try{
      assertTrue( theServer.start() );
      
      Message theMessage = new Message();
      theMessage.setDestination( theRoutingProtocol.getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( "PRCCMDcmd.exe /c dir" );
      theMessage.setProtocolMessage( true );
      String theResult = theMessageProtocol.sendMessage( theMessage );
      assertTrue( theResult.contains( "Volume Serial Number" ) );
    }finally{
      theServer.stop();
    }
  }

}
