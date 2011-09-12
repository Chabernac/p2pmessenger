/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public abstract class AbstractPacketProtocol {
  protected PacketProtocol myPacketProtocol;
  
  public abstract String getId();
  public abstract void handlePacket(Packet aPacket);
  
  public PacketProtocol getPacketProtocol() {
    return myPacketProtocol;
  }
  
  public void setPacketProtocol( PacketProtocol aPacketProtocol ) {
    myPacketProtocol = aPacketProtocol;
  }
  
  public abstract void stop();
}
