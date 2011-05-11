/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import chabernac.comet.CometEvent;
import chabernac.comet.CometException;
import chabernac.comet.EndPoint;
import chabernac.comet.EndPointContainer;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.WebPeer;

public class WebToPeerSenderTest extends TestCase {
  public void testWebToPeerSender() throws InterruptedException, IOException, CometException{
    EndPointContainer theContainer = new EndPointContainer();

    WebPeer theSendingPeer = new WebPeer("1", new URL("http://brol"));
    theSendingPeer.setEndPointContainer( theContainer );

    SocketPeer theReceivingPeer = new SocketPeer( "2" );

    final EndPoint theEndPoint = new EndPoint("2");
    theContainer.addEndPoint( theEndPoint );

    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        try{
          CometEvent theEvent = theEndPoint.getEvent();
          assertEquals( "input", theEvent.getInput());
          theEvent.setOutput( "reply" );
        }catch(Exception e){

        }
      }
    });

    WebToPeerSender theSender = new WebToPeerSender();
    String theReply = theSender.sendMessageTo( theSendingPeer, theReceivingPeer, "input", 5 );
    assertEquals( "reply", theReply );
  }
  
  public void testWebToPeerSenderNoEndPoint() throws IOException{
    EndPointContainer theContainer = new EndPointContainer();

    WebPeer theSendingPeer = new WebPeer("1", new URL("http://brol"));
    theSendingPeer.setEndPointContainer( theContainer );

    SocketPeer theReceivingPeer = new SocketPeer( "2" );
    
    WebToPeerSender theSender = new WebToPeerSender();
    try{
      theSender.sendMessageTo( theSendingPeer, theReceivingPeer, "input", 5 );
      fail("We should not come here");
    }catch(Exception e){
    }
  }
  
  public void testWebToPeerSenderNoEndPointContainer() throws IOException{

    WebPeer theSendingPeer = new WebPeer("1", new URL("http://brol"));

    SocketPeer theReceivingPeer = new SocketPeer( "2" );
    
    WebToPeerSender theSender = new WebToPeerSender();
    try{
      theSender.sendMessageTo( theSendingPeer, theReceivingPeer, "input", 5 );
      fail("We should not come here");
    }catch(Exception e){
    }
  }
}
