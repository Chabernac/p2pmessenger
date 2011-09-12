/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import chabernac.protocol.cam.CamProtocol;

public class PacketProtocolFactory {
  private Map<String, AbstractPacketProtocol> myProtocols = new HashMap<String, AbstractPacketProtocol>();
  private final PacketProtocol myPacketProtocol;
  
  
  public PacketProtocolFactory( PacketProtocol aPacketProtocol ) {
    super();
    myPacketProtocol = aPacketProtocol;
  }

  public AbstractPacketProtocol getProtocol(String anId) throws PacketProtocolException{
    if(!myProtocols.containsKey( anId )){
      AbstractPacketProtocol theProtocol = createProtocol(anId);
      if(theProtocol == null) throw new PacketProtocolException("No packet protocol found with id '" + anId + "'");
      theProtocol.setPacketProtocol( myPacketProtocol );
      myProtocols.put(anId, theProtocol);
    }
    return myProtocols.get(anId);
  }

  private AbstractPacketProtocol createProtocol( String anId ) {
    if(CamProtocol.ID.equalsIgnoreCase( anId )) return new CamProtocol();
    return null;
  }
  
  public Set<AbstractPacketProtocol> getPacketProtocols(){
    return Collections.unmodifiableSet( new HashSet<AbstractPacketProtocol>(myProtocols.values() ));
  }
}
