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

  public void testPacketStringConverterWithHeaders(){
    PacketStringConverter theConverter = new PacketStringConverter();
    Packet thePacket = new Packet( "1", "2", "id", "bytes;param1;param2", 3, true);
    thePacket.setHeader("a", "1");
    thePacket.setHeader("b", "2");
    String theString = theConverter.toString( thePacket );
    Packet theNewPacket = theConverter.getObject( theString );

    assertEquals( thePacket.getFrom(), theNewPacket.getFrom() );
    assertEquals( thePacket.getTo(), theNewPacket.getTo() );
    assertEquals( thePacket.getId(), theNewPacket.getId() );
    assertEquals( thePacket.getHopDistance(), theNewPacket.getHopDistance() );
    assertEquals( thePacket.getBytesAsString(), theNewPacket.getBytesAsString() );

    assertEquals( 2, theNewPacket.getHeaders().size() );
    assertEquals( "1", thePacket.getHeader( "a" ) );
    assertEquals( "2", thePacket.getHeader( "b" ) );
  }


}
