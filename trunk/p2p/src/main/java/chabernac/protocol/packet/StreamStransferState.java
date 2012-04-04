/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;

public class StreamStransferState extends AbstractTransferState {
  private final PacketProtocol myPacketProtocol;

  public StreamStransferState( PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Direction aDirection ) {
    super( aTransferId, aRemotePeer, aDirection );
    myPacketProtocol = aPacketProtocol;
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    PacketTransferComposite theTransfer = new PacketTransferComposite();
    theTransfer.addPacketTransfer( new PacketOutputStream( myRemotePeer, myTransferId, myPacketProtocol ));
    theTransfer.addPacketTransfer( new PacketInputStream( myPacketProtocol, myTransferId ));
    return theTransfer;
  }

  @Override
  public String getTransferDescription() {
    return "stream transfer";
  }

}
