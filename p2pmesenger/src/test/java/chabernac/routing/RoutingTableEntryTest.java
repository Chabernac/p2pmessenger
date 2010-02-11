/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

public class RoutingTableEntryTest extends TestCase {
  public void testRoutingTableEntryToString() throws FileNotFoundException{
    Peer thePeer = new Peer(1);
    thePeer.setHost( "x20d1148" );
    thePeer.setPort( 1000 );
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 2, thePeer);
    
    ByteArrayOutputStream theOut = new ByteArrayOutputStream();
    XMLEncoder theEncoder = new XMLEncoder(theOut);
    theEncoder.writeObject( theEntry );
    theEncoder.flush();
    theEncoder.close();
    
    String theXML = theOut.toString();
    System.out.println(theXML);
    theXML = theXML.replaceAll( "\r", "" );
    theXML = theXML.replaceAll( "\n", "" );
    System.out.println(theXML);
    
    XMLDecoder theDecoder = new XMLDecoder(new ByteArrayInputStream(theXML.getBytes()));
    
    RoutingTableEntry theNewEntry = (RoutingTableEntry) theDecoder.readObject();
    
//    System.out.println( theOut.toString() );
//    
//    System.out.println(theEntry.toString());
  }
}

