/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public interface iPacketListener {
  public void packetReceived(Packet aPacket);
  public void packetDelivered(String aPacketId);
  public void packetDeliveryFailed(String aPacketId);
}
