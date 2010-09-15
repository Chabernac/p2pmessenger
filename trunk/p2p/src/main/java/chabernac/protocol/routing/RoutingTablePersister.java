/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chabernac.io.iObjectPersister;

public class RoutingTablePersister implements iObjectPersister<RoutingTable> {
  private static long LAST_ONLINE_TIME = 10 * 24 * 60 * 60 * 1000;

  @Override
  public RoutingTable loadObject( InputStream anInputStream ) throws IOException {
    BufferedReader theReader = new BufferedReader(new InputStreamReader(anInputStream));
    String thePeerId = theReader.readLine().split( ";" )[1];
    RoutingTable theTable = new RoutingTable(thePeerId);
    theReader.readLine();
    
    //read peers
    theReader.readLine();
    Map<String, Peer> thePeers = new HashMap< String, Peer >();
    String theLine = null;
    while( !(theLine = theReader.readLine()).equals( "" )  ){
      String[] thePeerVars = theLine.split( ";" );
      Peer thePeer = new Peer();
      thePeer.setPeerId( thePeerVars[0]  );
      thePeer.setPort( Integer.parseInt( thePeerVars[1] ) );
      List<String> theHosts = new ArrayList< String >();
      for(int i=2;i<thePeerVars.length;i++){
        theHosts.add(thePeerVars[i]);
      }
      thePeer.setHosts( theHosts );
      thePeers.put( thePeer.getPeerId(), thePeer );
    }
    
    theReader.readLine();
    
    theLine = null;
    while( (theLine = theReader.readLine()) != null  ){
      String[] theRoutingTableEntryVars = theLine.split( ";" );
      long theLastOnlineTime = System.currentTimeMillis();
      if(theRoutingTableEntryVars.length >= 4){
        theLastOnlineTime = Long.parseLong( theRoutingTableEntryVars[3] );
      }
      RoutingTableEntry theEntry = new RoutingTableEntry(thePeers.get(  theRoutingTableEntryVars[0] ), Integer.parseInt(theRoutingTableEntryVars[1]), thePeers.get(theRoutingTableEntryVars[2]), theLastOnlineTime);
//      theEntry.setPeer( thePeers.get(  theRoutingTableEntryVars[0] ) ) ;
//      theEntry.setHopDistance( Integer.parseInt(theRoutingTableEntryVars[1] ));
//      theEntry.setGateway( thePeers.get(theRoutingTableEntryVars[2] )) ;
      
      //TODO is this clean?
      //only load the entries which are within the range of routing protocol
      if(RoutingProtocol.isInPortRange( theEntry.getPeer().getPort() )){
        theTable.addRoutingTableEntry( theEntry );
      }
    }
    
    return theTable;
  }

  @Override
  public void persistObject( RoutingTable aRoutingTable, OutputStream anOutputStream ) {
    PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(anOutputStream));
    theWriter.println("local peer id;" + aRoutingTable.getLocalPeerId());
    theWriter.println();
    
    //peers
    theWriter.println("Peer id;Port;Host");
    Set<Peer> thePeers = aRoutingTable.getAllPeers();
    for(Peer thePeer : thePeers){
      theWriter.print(thePeer.getPeerId() );
      theWriter.print(";");
      theWriter.print(thePeer.getPort() );
      for(String theHost : thePeer.getHosts()){
        theWriter.print(";");
        theWriter.print(theHost);
      }
      theWriter.println();
    }
    theWriter.println();
    //table
    theWriter.println("Peer id;Hops;Gateway");
    for(RoutingTableEntry theEntry: aRoutingTable.getEntries()){
      theWriter.print(theEntry.getPeer().getPeerId());
      theWriter.print(";");
      theWriter.print(theEntry.getHopDistance());
      theWriter.print(";");
      theWriter.print(theEntry.getGateway().getPeerId());
      theWriter.print(";");
      theWriter.print(theEntry.getLastOnlineTime());
      theWriter.println();
    }
    theWriter.flush();
    
  }

}
