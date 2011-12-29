/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractPacketTransfer implements iPacketTransfer {
  private Set< iPacketTransferListener > myTransferListeners = new HashSet< iPacketTransferListener >();
  
  private ExecutorService myListenerService = Executors.newFixedThreadPool( 1 );

  @Override
  public synchronized void addPacketTransferListener( iPacketTransferListener aTransferListener ) {
    myTransferListeners.add(aTransferListener);
  }

  @Override
  public synchronized void removePacketTransferListener( iPacketTransferListener aTransferListener ) {
    myTransferListeners.remove( myTransferListeners );
  }
  
  private synchronized Set<iPacketTransferListener> getPacketTransferListeners(){
    return Collections.unmodifiableSet( myTransferListeners );
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
