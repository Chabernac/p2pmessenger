/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import junit.framework.TestCase;

public class PacketTest extends TestCase {
  public void testPacket(){
    Packet thePacket = new Packet( "from", "to", "id", "listenerid", "bytes", 5, true);
    
    assertEquals( "from", thePacket.getFrom() );
    assertEquals( "to", thePacket.getTo() );
    assertEquals( "id", thePacket.getId() );
    assertEquals( "listenerid", thePacket.getListenerId() );
    assertEquals( 5, thePacket.getHopDistance());
    assertEquals( true, thePacket.isSendResponse());
   }
  
  public void testSetFrom(){
    Packet thePacket = new Packet( "from", "to", "id", "listenerid", "bytes", 5, true);
    
    thePacket = thePacket.setFrom( "from2" );
    
    assertEquals( "from2", thePacket.getFrom() );
    assertEquals( "to", thePacket.getTo() );
    assertEquals( "id", thePacket.getId() );
    assertEquals( "listenerid", thePacket.getListenerId() );
    assertEquals( 5, thePacket.getHopDistance());
    assertEquals( true, thePacket.isSendResponse());
  }
  
  public void testDecreaseHopDistance(){
    Packet thePacket = new Packet( "from", "to", "id", "listenerid", "bytes", 5, true);
    
    thePacket = thePacket.decreaseHopDistance();

    assertEquals( "from", thePacket.getFrom() );
    assertEquals( "to", thePacket.getTo() );
    assertEquals( "id", thePacket.getId() );
    assertEquals( "listenerid", thePacket.getListenerId() );
    assertEquals( 4, thePacket.getHopDistance());
    assertEquals( true, thePacket.isSendResponse());
  }
}
