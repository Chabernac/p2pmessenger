/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;

public interface iDataPacketProvider {
  public DataPacket getNextPacket() throws IOException;
  public boolean hasNextPacket();
  public DataPacket getPacket(String aPacketId) throws IOException;
  public int getNrOfPackets();
  public void releasePacket(String aPacketId);
}
