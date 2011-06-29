/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.AbstractPeer;

public class FilePacketSender {
  
  private final AsyncMessageProcotol myMessageProtocol;
  private final FilePacketIO myFilePacket;
  private final boolean[] mySendPackets;
  private final AbstractPeer myDestination;
  
  public FilePacketSender( AsyncMessageProcotol aMessageProtocol, FilePacketIO aFilePacket, AbstractPeer aDestination ) {
    super();
    myMessageProtocol = aMessageProtocol;
    myFilePacket = aFilePacket;
    mySendPackets = new boolean[aFilePacket.getNrOfPackets()];
    myDestination = aDestination;
  }
  
  public void send() throws IOException, InterruptedException{
    ExecutorService theSerice = Executors.newFixedThreadPool( 2 );
    
    
    final CountDownLatch theLatch = new CountDownLatch( myFilePacket.getNrOfPackets() );
    for(int i=0;i<=myFilePacket.getNrOfPackets();i++){
      final int thePacket = i;
    
      final Message theMessage = new Message();
      theMessage.setDestination( myDestination );
      theMessage.addHeader( "FILE", myFilePacket.getFile().getName());
      theMessage.addHeader( "PACKETSIZE", Integer.toString(myFilePacket.getPacketSize()));
      theMessage.addHeader( "PACKET", Integer.toString( i ));
      theMessage.setBytes( myFilePacket.getPacket( i ) );
      
      theSerice.execute( new Runnable(){
        public void run(){
          try{
            myMessageProtocol.sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
            mySendPackets[thePacket] = true;
          }catch(Exception e){
            mySendPackets[thePacket] = false;
          } finally {
            theLatch.countDown();
          }
        }
      });
    }
    
    theLatch.await( 5, TimeUnit.SECONDS );
    
  }
  
  

}
