/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import chabernac.io.iObjectPersister;

public class RoutingTableObjectPersister implements iObjectPersister< RoutingTable > {

  @Override
  public RoutingTable loadObject( InputStream anInputStream ) throws IOException {
    ObjectInputStream theObjectInputStream = new ObjectInputStream(anInputStream);
    try {
      return (RoutingTable)theObjectInputStream.readObject();
    } catch ( ClassNotFoundException e ) {
      throw new IOException();
    }
  }

  @Override
  public void persistObject( RoutingTable aRoutingTable, OutputStream anOutputStream ) throws IOException {
    RoutingTable theTableToPersist = new RoutingTable( aRoutingTable.getLocalPeerId() );
    
    for(RoutingTableEntry theEntry : aRoutingTable.getEntries()){
      if(!theEntry.getPeer().isTemporaryPeer()){
        theTableToPersist.addEntry(theEntry);
      }
    }
    
    ObjectOutputStream theObjectOutputStream = new ObjectOutputStream(anOutputStream);
    theObjectOutputStream.writeObject( theTableToPersist );
  }
  

}
