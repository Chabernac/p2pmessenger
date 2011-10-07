/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;

public interface iDataPacketPersister {
  public void persistDataPacket(DataPacket aPacket) throws IOException;
}
