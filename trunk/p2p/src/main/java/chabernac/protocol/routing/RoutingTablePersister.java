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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import chabernac.io.iObjectPersister;

public class RoutingTablePersister implements iObjectPersister<RoutingTable> {
  private static Logger LOGGER = Logger.getLogger( RoutingTablePersister.class );

  @Override
  public RoutingTable loadObject( InputStream anInputStream ) throws IOException {
    BufferedReader theReader = new BufferedReader(new InputStreamReader(anInputStream));
    String thePeerId = theReader.readLine().split( ";" )[1];
    RoutingTable theTable = new RoutingTable(thePeerId);
    theReader.readLine();

    //read peers
    theReader.readLine();
    Map<String, AbstractPeer> thePeers = new HashMap< String, AbstractPeer >();
    String theLine = null;
    while( !(theLine = theReader.readLine()).equals( "" )  ){
      String[] thePeerVars = theLine.split( ";" );
      AbstractPeer thePeer = null;
      if(thePeerVars.length == 2){
        thePeer = new WebPeer(thePeerVars[0], new URL(thePeerVars[1]));
      } else {
        thePeer = new SocketPeer();
        SocketPeer theSocketPeer = (SocketPeer)thePeer;
        theSocketPeer.setPeerId( thePeerVars[0]  );
        theSocketPeer.setPort( Integer.parseInt( thePeerVars[1] ) );
        List<String> theHosts = new ArrayList< String >();
        for(int i=2;i<thePeerVars.length;i++){
          theHosts.add(thePeerVars[i]);
        }
        theSocketPeer.setHosts( theHosts );
      }
      thePeers.put( thePeer.getPeerId(), thePeer );
    }

    theReader.readLine();

    theLine = null;
    while( (theLine = theReader.readLine()) != null  ){
      try{
      String[] theRoutingTableEntryVars = theLine.split( ";" );
      if(theRoutingTableEntryVars.length >=3){
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
        if(theEntry.getPeer() instanceof SocketPeer && RoutingProtocol.isInPortRange( ((SocketPeer)theEntry.getPeer()).getPort() )){
          theTable.addRoutingTableEntry( theEntry );
        } else if(theEntry.getPeer() instanceof WebPeer){
          theTable.addRoutingTableEntry( theEntry );
        }
      }
      }catch(Exception e){
        LOGGER.error("Could not parse line '" + theLine + "' as routing table entry", e);
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
    Set<AbstractPeer> thePeers = aRoutingTable.getAllPeers();
    for(AbstractPeer thePeer : thePeers){
      //TODO this can be done in a better way
      if(thePeer instanceof SocketPeer){
        SocketPeer theSPeer = (SocketPeer)thePeer;
        theWriter.print(thePeer.getPeerId() );
        theWriter.print(";");
        theWriter.print(theSPeer.getPort() );
        for(String theHost : theSPeer.getHosts()){
          theWriter.print(";");
          theWriter.print(theHost);
        }
        theWriter.println();
      } else if(thePeer instanceof WebPeer){
        WebPeer theWPeer = (WebPeer)thePeer;
        theWriter.print(thePeer.getPeerId() );
        theWriter.print(";");
        theWriter.print(theWPeer.getURL());
        theWriter.println();
      }
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
