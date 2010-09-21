/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import chabernac.comet.DataHandlingException;
import chabernac.comet.AbstractDataHandler;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.routing.PeerSenderHolder;
import chabernac.tools.PropertyMap;

public class P2PDataHandler extends AbstractDataHandler{
  private final ProtocolContainer myProtocolContainer;
  
  public P2PDataHandler(){
    PropertyMap thePropertyMap = new PropertyMap();
    thePropertyMap.setProperty("routingprotocol.exchangedelay", "-1");
    thePropertyMap.setProperty("routingprotocol.persist", "false");
    myProtocolContainer = new ProtocolContainer(new ProtocolFactory(thePropertyMap));
    
    PeerSenderHolder.setPeerSender(new WebPeerSender());
  }
  

  public String handleData( String aData ) throws DataHandlingException{
      return myProtocolContainer.handleCommand(-1, aData);
  }
}
