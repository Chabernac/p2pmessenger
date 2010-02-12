/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import junit.framework.TestCase;

public class XMLToolsTest extends TestCase {
  public void testToXML(){
    Peer thePeer0 = new Peer(1, "localhost", 1001);
    RoutingTable theTable = new RoutingTable(thePeer0);

    Peer thePeer = new Peer(2, "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer);

    theTable.addRoutingTableEntry( theEntry2 );

    String theString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <java version=\"1.6.0_05\" class=\"java.beans.XMLDecoder\">  <object class=\"chabernac.protocol.routing.RoutingTable\">   <void property=\"peer\">    <object class=\"chabernac.protocol.routing.Peer\">     <void property=\"host\">      <string>localhost</string>     </void>     <void property=\"peerId\">      <long>1</long>     </void>     <void property=\"port\">      <int>1001</int>     </void>    </object>   </void>   <void property=\"routingTable\">    <void method=\"put\">     <object id=\"Peer0\" class=\"chabernac.protocol.routing.Peer\">      <void property=\"host\">       <string>localhost</string>      </void>      <void property=\"peerId\">       <long>2</long>      </void>      <void property=\"port\">       <int>1002</int>      </void>     </object>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object idref=\"Peer0\"/>      </void>      <void property=\"hopDistance\">       <int>2</int>      </void>      <void property=\"peer\">       <object idref=\"Peer0\"/>      </void>     </object>    </void>   </void>  </object> </java> ";
    assertEquals( theString, XMLTools.toXML( theTable ));
  }
  
  public void fromXML(){
    String theString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <java version=\"1.6.0_05\" class=\"java.beans.XMLDecoder\">  <object class=\"chabernac.protocol.routing.RoutingTable\">   <void property=\"peer\">    <object class=\"chabernac.protocol.routing.Peer\">     <void property=\"host\">      <string>localhost</string>     </void>     <void property=\"peerId\">      <long>1</long>     </void>     <void property=\"port\">      <int>1001</int>     </void>    </object>   </void>   <void property=\"routingTable\">    <void method=\"put\">     <object id=\"Peer0\" class=\"chabernac.protocol.routing.Peer\">      <void property=\"host\">       <string>localhost</string>      </void>      <void property=\"peerId\">       <long>2</long>      </void>      <void property=\"port\">       <int>1002</int>      </void>     </object>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object idref=\"Peer0\"/>      </void>      <void property=\"hopDistance\">       <int>2</int>      </void>      <void property=\"peer\">       <object idref=\"Peer0\"/>      </void>     </object>    </void>   </void>  </object> </java> ";
    RoutingTable theTable = (RoutingTable)XMLTools.fromXML( theString );
    assertNotNull( theTable );
    assertEquals( "localhost", theTable.getEntries().get( 0 ).getPeer().getHost());
    assertEquals( 1, theTable.getEntries().get( 0 ).getHopDistance());
  }
}
