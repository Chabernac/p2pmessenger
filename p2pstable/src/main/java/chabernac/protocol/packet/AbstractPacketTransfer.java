/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractPacketTransfer implements iPacketTransfer {
  private List< iPacketTransferListener > myTransferListeners = new ArrayList< iPacketTransferListener >();
  
  private ExecutorService myListenerService = Executors.newFixedThreadPool( 1 );

  @Override
  public synchronized void addPacketTransferListener( iPacketTransferListener aTransferListener ) {
    myTransferListeners.add(aTransferListener);
  }

  @Override
  public synchronized void removePacketTransferListener( iPacketTransferListener aTransferListener ) {
    myTransferListeners.remove( myTransferListeners );
  }
  
  private synchronized List<iPacketTransferListener> getPacketTransferListeners(){
    return Collections.unmodifiableList( myTransferListeners );
  }
  
  protected void notifyListeners(){
    final PacketTransferState theState = getTransferState();
    myListenerService.execute( new Runnable(){
      public void run(){
        for(iPacketTransferListener theListener : getPacketTransferListeners()){
          theListener.transferUpdated( theState );
        }
      }
    });
  }
}
