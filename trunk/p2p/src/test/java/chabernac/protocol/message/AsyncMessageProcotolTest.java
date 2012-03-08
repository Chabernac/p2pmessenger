/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.iP2PServer;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.testingutils.MessageCounterListener;
import chabernac.tools.BlockingProtocol;

public class AsyncMessageProcotolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(AsyncMessageProcotolTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testSendEndUserMessage() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer(theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    AsyncMessageProcotol theMessageProtocol1 = (AsyncMessageProcotol)theProtocol1.getProtocol( AsyncMessageProcotol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    AsyncMessageProcotol theMessageProtocol2 = (AsyncMessageProcotol)theProtocol2.getProtocol( AsyncMessageProcotol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );

      MessageCounterListener theListener = new MessageCounterListener();
      theMessageProtocol2.addMessageListener( theListener );

      Message theMessage = new Message();
      theMessage.setDestination( theRoutingTable1.getEntryForPeer( "2" ).getPeer() );
      int times = 10;
      for(int i=0;i<times;i++){
        theMessage.setMessage( "test message " + i );
        System.out.println(i);
        theMessageProtocol1.sendAndWaitForResponse( theMessage, 50, TimeUnit.SECONDS );
      }

      assertEquals( times, theListener.getCounter() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testCancelResponse() throws ProtocolException, InterruptedException, UnknownPeerException, MessageException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer(theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    theProtocol2.addProtocol( new BlockingProtocol( 5000 ) );
    iP2PServer theServer2 = getP2PServer(theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    final AsyncMessageProcotol theMessageProtocol1 = (AsyncMessageProcotol)theProtocol1.getProtocol( AsyncMessageProcotol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
//    AsyncMessageProcotol theMessageProtocol2 = (AsyncMessageProcotol)theProtocol2.getProtocol( AsyncMessageProcotol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );
      final Message theMessage = new Message();
      theMessage.setDestination( theRoutingTable1.getEntryForPeer( "2" ).getPeer() );
      theMessage.setProtocolMessage( true );
      theMessage.setMessage( "BLPTest" );
      theMessageProtocol1.sendMessage( theMessage );
      theMessageProtocol1.cancelResponse( theMessage.getMessageId().toString() );
      final CountDownLatch myExceptionCountDown = new CountDownLatch(1);
      new Thread(new Runnable(){
        public void run(){
          try{
            theMessageProtocol1.getResponse( theMessage.getMessageId().toString(), 1, TimeUnit.SECONDS );
            fail("Should not get here get response must have thrown exception");
          }catch(Exception e){
            myExceptionCountDown.countDown();
          }
        }
      }).start();
      Thread.sleep(1000);
      new Thread(new Runnable(){
        public void run(){
          try {
            theMessageProtocol1.cancelResponse( theMessage.getMessageId().toString() );
          } catch (InterruptedException e) {
          }
        }
      }).start();
      
      myExceptionCountDown.await(2, TimeUnit.SECONDS);
      assertEquals(0, myExceptionCountDown.getCount());

      final CountDownLatch theCancelCounter = new CountDownLatch(5);
      for(int i=0;i<5;i++){
        new Thread(new Runnable(){
          public void run(){
            try {
              theMessageProtocol1.cancelResponse( theMessage.getMessageId().toString() );
              theCancelCounter.countDown();
            } catch (InterruptedException e) {
            }
          }
        }).start();
      }
      
     theCancelCounter.await(2, TimeUnit.SECONDS);
     assertEquals(0, theCancelCounter.getCount());
        
    } finally {
      theServer1.stop();
      theServer2.stop();
    }

  }
  
  public void testMessageLoop() throws ProtocolException, InterruptedException, UnknownPeerException, MessageException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer(theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer(theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    AsyncMessageProcotol theMessageProtocol1 = (AsyncMessageProcotol)theProtocol1.getProtocol( AsyncMessageProcotol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep(SLEEP_AFTER_SCAN);


      AbstractPeer thePeer1 = theRoutingTable1.getEntryForLocalPeer().getPeer();
      AbstractPeer thePeer2 = theRoutingTable2.getEntryForLocalPeer().getPeer();

      SocketPeer thePeer3 = new SocketPeer("3", 124, "brol");
      thePeer3.setChannel(thePeer1.getChannel());

      theRoutingTable1.addRoutingTableEntry(new RoutingTableEntry(thePeer3, 2, thePeer2, System.currentTimeMillis()));
      theRoutingTable2.addRoutingTableEntry(new RoutingTableEntry(thePeer3, 2, thePeer1, System.currentTimeMillis()));

      Message theMessage = new Message();
      theMessage.setDestination(thePeer3);
      theMessage.setMessage("test");
      try{
        theMessageProtocol1.sendAndWaitForResponse(theMessage);
        fail("Should not get here");
      }catch(MessageException e){
        assertEquals(MessageProtocol.Response.MESSAGE_LOOP_DETECTED, e.getResponse());
      }

    } finally {
      if(theServer1 != null) theServer1.stop();
      if(theServer2 != null) theServer2.stop();
    }
  }
}
