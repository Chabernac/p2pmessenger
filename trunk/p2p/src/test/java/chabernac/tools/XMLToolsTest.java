/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import chabernac.protocol.message.Message;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;
import junit.framework.TestCase;

public class XMLToolsTest extends TestCase {
  public void testToXML() throws UnknownPeerException{
    RoutingTable theTable = new RoutingTable("1");

    Peer thePeer = new Peer("2", "x20d1148", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    
    Peer thePeer2 = new Peer("3", "x01p0880", 1003);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer2, 2, thePeer);

    theTable.addRoutingTableEntry( theEntry );
    theTable.addRoutingTableEntry( theEntry2 );
    
    RoutingTable theTable2 = (RoutingTable)XMLTools.fromXML(XMLTools.toXML( theTable ));
    
    assertEquals(2, theTable2.getEntries().size());
    
   assertEquals("x20d1148", theTable2.getEntryForPeer( "2" ).getPeer().getHosts().get( 0 ));
   assertEquals(1002, theTable2.getEntryForPeer( "2" ).getPeer().getPort());
   assertEquals("2", theTable2.getEntryForPeer( "2" ).getPeer().getPeerId());
   
   assertEquals("x01p0880", theTable2.getEntryForPeer( "3" ).getPeer().getHosts().get( 0 ));
   assertEquals(1003, theTable2.getEntryForPeer( "3" ).getPeer().getPort());
   assertEquals("3", theTable2.getEntryForPeer( "3" ).getPeer().getPeerId());

  }
  
  public void testPeerEntryToXML(){
    Peer thePeer = new Peer("2", "x20d1148", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    RoutingTableEntry theEntry2 = (RoutingTableEntry)XMLTools.fromXML( XMLTools.toXML( theEntry ));
    assertEquals( theEntry.getPeer().getPeerId(), theEntry2.getPeer().getPeerId()); 
  }
  
  public void testFromXML(){
    String theXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <java version=\"1.6.0_05\" class=\"java.beans.XMLDecoder\">  <object class=\"chabernac.protocol.routing.RoutingTable\">   <void property=\"localPeerId\">    <string>3</string>   </void>   <void id=\"HashMap0\" property=\"routingTable\">    <void method=\"put\">     <string>3</string>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object id=\"Peer0\" class=\"chabernac.protocol.routing.Peer\">        <void property=\"hosts\">         <object class=\"java.util.ArrayList\">          <void method=\"add\">           <string>10.240.114.251</string>          </void>         </object>        </void>        <void property=\"peerId\">         <string>3</string>        </void>        <void property=\"port\">         <int>12702</int>        </void>       </object>      </void>      <void property=\"hopDistance\">       <int>0</int>      </void>      <void property=\"peer\">       <object idref=\"Peer0\"/>      </void>     </object>    </void>    <void method=\"put\">     <string>2</string>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object id=\"Peer1\" class=\"chabernac.protocol.routing.Peer\">        <void property=\"hosts\">         <object class=\"java.util.ArrayList\">          <void method=\"add\">           <string>10.240.114.251</string>          </void>         </object>        </void>        <void property=\"peerId\">         <string>2</string>        </void>        <void property=\"port\">         <int>12701</int>        </void>       </object>      </void>      <void property=\"hopDistance\">       <int>1</int>      </void>      <void property=\"peer\">       <object idref=\"Peer1\"/>      </void>     </object>    </void>    <void method=\"put\">     <string>1</string>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object id=\"Peer2\" class=\"chabernac.protocol.routing.Peer\">        <void property=\"hosts\">         <object class=\"java.util.ArrayList\">          <void method=\"add\">           <string>10.240.114.251</string>          </void>         </object>        </void>        <void property=\"peerId\">         <string>1</string>        </void>        <void property=\"port\">         <int>12700</int>        </void>       </object>      </void>      <void property=\"hopDistance\">       <int>1</int>      </void>      <void property=\"peer\">       <object idref=\"Peer2\"/>      </void>     </object>    </void>    <void method=\"put\">     <string>4</string>     <object class=\"chabernac.protocol.routing.RoutingTableEntry\">      <void property=\"gateway\">       <object id=\"Peer3\" class=\"chabernac.protocol.routing.Peer\">        <void property=\"hosts\">         <object class=\"java.util.ArrayList\">          <void method=\"add\">           <string>10.240.114.251</string>          </void>         </object>        </void>        <void property=\"peerId\">         <string>4</string>        </void>        <void property=\"port\">         <int>12703</int>        </void>       </object>      </void>      <void property=\"hopDistance\">       <int>1</int>      </void>      <void property=\"peer\">       <object idref=\"Peer3\"/>      </void>     </object>    </void>   </void>   <void property=\"routingTable\">    <object idref=\"HashMap0\"/>   </void>  </object> </java> ";
    RoutingTable theTable = (RoutingTable)XMLTools.fromXML(theXML);
    for(RoutingTableEntry theEntry : theTable){
      assertNotNull( theEntry.getPeer() );
      assertNotNull( theEntry.getGateway() );
    }
  }
  
  public void testCarriageReturnLineFeed(){
    String theString = "the \r\n carriage return";
    String theXML = XMLTools.toXML( theString );
    assertFalse( theXML.contains( "\r\n" ) );
    assertEquals( theString, XMLTools.fromXML( theXML ) );
    
  }
  
  public void testBytesToXML(){
    Message theMessage = new Message();
    byte[] theBytes = new byte[100];
    for(int i=0;i<theBytes.length;i++){
      theBytes[i] = (byte)i;
    }
    theMessage.setBytes( theBytes );
    
    String theXML = XMLTools.toXML( theMessage );
    Message theNewMessage = (Message)XMLTools.fromXML( theXML );
    
    assertNotNull( theNewMessage );
    assertNotNull( theNewMessage.getBytes() );
    
    byte[] theNewBytes = theNewMessage.getBytes();
    
    assertEquals( theBytes.length, theNewBytes.length );
    
    for(int i=0;i<theBytes.length;i++){
      assertEquals( theBytes[i], theNewBytes[i] );
    }
    
  }
  
}
