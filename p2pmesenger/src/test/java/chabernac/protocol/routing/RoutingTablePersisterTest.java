/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import chabernac.io.persist.iObjectPersister;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.RoutingTablePersister;

public class RoutingTablePersisterTest extends TestCase {
  public void testPersistRoutingTable() throws IOException{
    RoutingTable theTable = new RoutingTable("1");
    
    Peer thePeer1 = new Peer("1");
    List<String> theList = new ArrayList< String >();
    theList.add( "x20d1148" );
    theList.add( "localhost" );
    thePeer1.setPort( 123 );
    thePeer1.setHosts( theList );
    
    Peer thePeer2 = new Peer("2");
    List<String> theList2 = new ArrayList< String >();
    theList2.add( "x20d1149" );
    theList2.add( "localhost" );
    thePeer2.setPort( 124 );
    thePeer2.setHosts( theList2 );
    
    Peer thePeer3 = new Peer("3");
    List<String> theList3 = new ArrayList< String >();
    theList3.add( "x20d1150" );
    theList3.add( "localhost" );
    thePeer3.setPort( 125 );
    thePeer3.setHosts( theList3 );
    
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer1, 0, thePeer1) );
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer2, 1, thePeer2) );
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer3, 2, thePeer2) );
    
    File theFile = new File("routingtable.csv");
    
    iObjectPersister< RoutingTable > thePersister = new RoutingTablePersister();
    FileOutputStream theOutputStream = new FileOutputStream(theFile);
    thePersister.persistObject( theTable, theOutputStream );
    theOutputStream.flush();
    theOutputStream.close();
    
    FileInputStream theInputStream = new FileInputStream(theFile);
    RoutingTable theTable2 = thePersister.loadObject( theInputStream );
    
    assertEquals( theTable.getEntries().size(), theTable2.getEntries().size() );
    assertEquals( theTable.getLocalPeerId(), theTable2.getLocalPeerId() );
                 
    List<RoutingTableEntry> theEntries1 = theTable.getEntries();
    List<RoutingTableEntry> theEntries2 = theTable2.getEntries();
    
    for(int i=0;i<theEntries1.size();i++){
      RoutingTableEntry theEntry = theEntries1.get( i );
      RoutingTableEntry theEntry2 = theEntries2.get( i );
      assertEquals( theEntry, theEntry2 );
    }
    
    theInputStream.close();
    
    assertTrue( theFile.delete() );
    
  }
}
