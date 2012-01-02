package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class DataPacketComparatorTest extends TestCase {
  public void testDataPacketComparator(){
    List<DataPacket> thePackets = new ArrayList<DataPacket>();
    thePackets.add(new DataPacket("5", null));
    thePackets.add(new DataPacket("4", null));
    thePackets.add(new DataPacket("10", null));
    thePackets.add(new DataPacket("1", null));
    thePackets.add(new DataPacket("100", null));
    
    Collections.sort(thePackets, new DataPacketComparator());
    
    assertEquals("1", thePackets.get(0).getId());
    assertEquals("4", thePackets.get(1).getId());
    assertEquals("5", thePackets.get(2).getId());
    assertEquals("10", thePackets.get(3).getId());
    assertEquals("100", thePackets.get(4).getId());
  }
}
