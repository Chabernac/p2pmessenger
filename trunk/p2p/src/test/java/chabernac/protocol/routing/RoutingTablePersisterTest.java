/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;
import chabernac.io.iObjectPersister;
import chabernac.tools.SimpleNetworkInterface;

public class RoutingTablePersisterTest extends TestCase {
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  
  public void testPersistRoutingTable() throws IOException{
    RoutingTable theTable = new RoutingTable("1");
    
    SocketPeer thePeer1 = new SocketPeer("1");
    List<SimpleNetworkInterface> theList = new ArrayList< SimpleNetworkInterface >();
    theList.add( new SimpleNetworkInterface("x20d1148") );
    theList.add( new SimpleNetworkInterface("localhost") );
    thePeer1.setPort( RoutingProtocol.START_PORT );
    thePeer1.setHosts( theList );
    
    SocketPeer thePeer2 = new SocketPeer("2");
    List<SimpleNetworkInterface> theList2 = new ArrayList< SimpleNetworkInterface >();
    theList2.add( new SimpleNetworkInterface("x20d1149") );
    theList2.add( new SimpleNetworkInterface("localhost") );
    thePeer2.setPort( RoutingProtocol.START_PORT + 1);
    thePeer2.setHosts( theList2 );
    
    SocketPeer thePeer3 = new SocketPeer("3");
    List<SimpleNetworkInterface> theList3 = new ArrayList< SimpleNetworkInterface >();
    theList3.add( new SimpleNetworkInterface("x20d1150") );
    theList3.add( new SimpleNetworkInterface("localhost") );
    thePeer3.setPort( RoutingProtocol.START_PORT + 2 );
    thePeer3.setHosts( theList3 );
    
    WebPeer thePeer4 = new WebPeer("4", new URL("http://localhost:8080/"));
    
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer1, 0, thePeer1, System.currentTimeMillis()) );
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer2, 1, thePeer2, System.currentTimeMillis()) );
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer3, 2, thePeer2, System.currentTimeMillis()) );
    theTable.addRoutingTableEntry( new RoutingTableEntry(thePeer4, 1, thePeer4, System.currentTimeMillis()) );
    
    File theFile = new File("routingtable.bin");
    
    iObjectPersister< RoutingTable > thePersister = new RoutingTableObjectPersister();
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
