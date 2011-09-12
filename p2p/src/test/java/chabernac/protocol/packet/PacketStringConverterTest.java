/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import junit.framework.TestCase;

public class PacketStringConverterTest extends TestCase {
  public void testPacketStringConverter(){
    PacketStringConverter theConverter = new PacketStringConverter();
    
    Packet thePacket = new Packet( "1", "2", "id", "bytes;param1;param2", 3, true);
    String theString = theConverter.toString( thePacket );
    Packet theNewPacket = theConverter.getObject( theString );
    
    assertEquals( thePacket.getFrom(), theNewPacket.getFrom() );
    assertEquals( thePacket.getTo(), theNewPacket.getTo() );
    assertEquals( thePacket.getId(), theNewPacket.getId() );
    assertEquals( thePacket.getHopDistance(), theNewPacket.getHopDistance() );
    assertEquals( thePacket.getBytesAsString(), theNewPacket.getBytesAsString() );
  }

}
