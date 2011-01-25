/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.UnknownPeerException;

public class MessageProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(MessageProtocolTest.class);

  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testMessageProtocol() throws ProtocolException, InterruptedException, SocketException, MessageException, UnknownPeerException, NoAvailableNetworkAdapterException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);


    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable3 = theRoutingProtocol3.getRoutingTable();

    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable3.getEntryForLocalPeer() );

      for(int i=0;i<5;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }

      RoutingTableEntry theRoutingTableEntry = theRoutingTable1.getEntryForPeer( "3" );

      assertEquals( 2, theRoutingTableEntry.getHopDistance() );

      Message theMessage = new Message();
      theMessage.setDestination( theRoutingTable1.getEntryForPeer( "3" ).getPeer() );
      theMessage.setMessage( "ECOTest" );
      theMessage.setProtocolMessage( true );
      assertEquals( "Test", theMessageProtocol1.sendMessage( theMessage ));


    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }
  }

  public void testSendEndUserMessage() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );

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
        theMessageProtocol1.sendMessage( theMessage );
      }

      assertEquals( times, theListener.getCounter() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }

  }

  public void testSendEnctryptedMessage() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );

      MessageCollector theListener = new MessageCollector();
      theMessageProtocol2.addMessageListener( theListener );

      Message theMessage = new Message();
      theMessage.addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
      theMessage.setDestination( theRoutingTable1.getEntryForPeer( "2" ).getPeer() );
      int times = 10;
      for(int i=0;i<times;i++){
        theMessage.setMessage( "test message " + i );
        theMessageProtocol1.sendMessage( theMessage );
      }

      assertEquals( times, theListener.getMessages().size() );

      for(int i=0;i<theListener.getMessages().size();i++){
        assertEquals( "test message " + i, theListener.getMessages().get( i ).getMessage() );
      }
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  public void testStressTest() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 6);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 6);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    final RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    final MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );

      MessageCollector theListener = new MessageCollector();
      theMessageProtocol2.addMessageListener( theListener );

      ExecutorService theservice = Executors.newFixedThreadPool( 10 );

      int times = 1000;
      final CountDownLatch theLatch = new CountDownLatch(times);

      for(int i=0;i<times;i++){
        final int theCurrMessage = i;
        theservice.execute( new Runnable (){
          public void run(){
            try{
              System.out.println("Sending message nr: " + theCurrMessage);
              Message theMessage = new Message();
              theMessage.addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
              theMessage.setDestination( theRoutingTable1.getEntryForPeer( "2" ).getPeer() );
              theMessage.setMessage( "test message");
              theMessageProtocol1.sendMessage( theMessage );
              theLatch.countDown();
              //              if(theLatch.getCount() % 100 == 0){
              System.out.println("message nr: " + theCurrMessage);
              //              }
            }catch(Exception e){
              e.printStackTrace();
            }
          }
        });

      }

      theLatch.await(40, TimeUnit.SECONDS);

      assertEquals( 0, theLatch.getCount() );

      assertEquals( times, theListener.getMessages().size() );

      for(int i=0;i<theListener.getMessages().size();i++){
        assertEquals( "test message", theListener.getMessages().get( i ).getMessage() );
      }
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  public void testMessageLoop() throws ProtocolException, InterruptedException, UnknownPeerException, MessageException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 6);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 6);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      Thread.sleep(SLEEP_AFTER_SCAN);


      AbstractPeer thePeer1 = theRoutingTable1.getEntryForLocalPeer().getPeer();
      AbstractPeer thePeer2 = theRoutingTable2.getEntryForLocalPeer().getPeer();

      SocketPeer thePeer3 = new SocketPeer("3","brol",124);
      thePeer3.setChannel(thePeer1.getChannel());

      theRoutingTable1.addRoutingTableEntry(new RoutingTableEntry(thePeer3, 2, thePeer2, System.currentTimeMillis()));
      theRoutingTable2.addRoutingTableEntry(new RoutingTableEntry(thePeer3, 2, thePeer1, System.currentTimeMillis()));

      Message theMessage = new Message();
      theMessage.setDestination(thePeer3);
      theMessage.setMessage("test");
      try{
        theMessageProtocol1.sendMessage(theMessage);
        fail("Should not get here");
      }catch(MessageException e){
        assertEquals(MessageProtocol.Response.MESSAGE_LOOP_DETECTED, e.getResponse());
      }

    } finally {
      if(theServer1 != null) theServer1.stop();
      if(theServer2 != null) theServer2.stop();
    }
  }

  public void testMessageFromUnknownPeer() throws ProtocolException, InterruptedException, UnknownPeerException, MessageException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 6);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 6);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();

      Thread.sleep( SLEEP_AFTER_SCAN );

      //peer 1 should now peer 2
      assertTrue( theRoutingTable1.containsEntryForPeer( theRoutingProtocol2.getLocalPeerId() ) );
      //but peer 2 should not know peer 1
      if(theRoutingTable2.containsEntryForPeer( theRoutingProtocol1.getLocalPeerId() )){
        //and if it does remove the entry
        theRoutingTable2.removeRoutingTableEntry( theRoutingTable2.getEntryForPeer( theRoutingProtocol1.getLocalPeerId() ) );
        Thread.sleep( SLEEP_AFTER_SCAN );
      }
      assertFalse( theRoutingTable2.containsEntryForPeer( theRoutingProtocol1.getLocalPeerId() ) );

      //now send a message from peer 1 to peer 2
      Message theMessage = new Message();
      theMessage.setDestination( theRoutingTable2.getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( "test" );

      theMessageProtocol1.sendMessage( theMessage );

      Thread.sleep( SLEEP_AFTER_SCAN );

      //if the message has been received by peer 2, the routing table of peer 2 must now contain peer 1
      assertTrue( theRoutingTable2.containsEntryForPeer( theRoutingProtocol1.getLocalPeerId() ) );


      //scanning the local system might take a small time
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  public class MessageCounterListener implements iMessageListener{
    private AtomicInteger myCounter = new AtomicInteger();

    @Override
    public void messageReceived( Message aMessage ) {
      myCounter.incrementAndGet();
    }

    public int getCounter(){
      return myCounter.get();
    }

    @Override
    public void messageUpdated( Message aMessage ) {
      // TODO Auto-generated method stub
      
    }
  }

  public class MessageCollector implements iMessageListener{
    private List<Message> myMessages = Collections.synchronizedList( new ArrayList< Message >() );

    @Override
    public void messageReceived( Message aMessage ) {
      myMessages.add(aMessage); 
    }

    public List<Message> getMessages(){
      return myMessages;
    }

    @Override
    public void messageUpdated( Message aMessage ) {
      // TODO Auto-generated method stub
      
    }
  }



}

