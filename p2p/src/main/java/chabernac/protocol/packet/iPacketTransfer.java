/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;


public interface iPacketTransfer {
  public void addPacketTransferListener(iPacketTransferListener aTransferListener);
  public void removePacketTransferListener(iPacketTransferListener aTransferListener);
  
  public void start();
  public void stop();
  public void done();
  public void waitUntillDone();
  public PacketTransferState getTransferState();
}
