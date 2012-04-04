/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketTransferComposite implements iPacketTransfer {
  
  private final List<iPacketTransfer> myPacketTransfers = new ArrayList<iPacketTransfer>();
  
  public void addPacketTransfer(iPacketTransfer aPacketTransfer){
    myPacketTransfers.add(aPacketTransfer);
  }

  @Override
  public void addPacketTransferListener( iPacketTransferListener aTransferListener ) {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.addPacketTransferListener( aTransferListener ); 
  }

  @Override
  public void removePacketTransferListener( iPacketTransferListener aTransferListener ) {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.removePacketTransferListener(  aTransferListener );
  }

  @Override
  public void start() {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.start();
  }

  @Override
  public void stop() {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.done();
  }

  @Override
  public void done() {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.done();
  }

  @Override
  public void waitUntillDone() {
    for(iPacketTransfer thePacketTransfer: myPacketTransfers) thePacketTransfer.waitUntillDone();
  }

  @Override
  public PacketTransferState getTransferState() {
    return null;
  }

}
