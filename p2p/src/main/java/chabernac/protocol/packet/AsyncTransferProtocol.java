/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class AsyncTransferProtocol extends Protocol implements iPacketTransferController {
  public static final String ID = "ATP";

  public AsyncTransferProtocol ( String anId ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Async transfer protocol";
  }
  
  private AsyncMessageProcotol getMessageProtocol() throws ProtocolException{
    return (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID);
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }


  @Override
  public void addPacketTransferListener( String aTransferId, iPacketTransferListener aPacketTransferListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void start( String aTransferId ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void stop( String aTransferId ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void waitUntillDone( String aTransferId ) {
    // TODO Auto-generated method stub

  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
