/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTransferState extends AbstractTransferState {
  private final PacketProtocol myPacketProtocol;
  private PacketOutputStream myOutputStream;
  private PacketInputStream myInputStream;

  public StreamTransferState( PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer ) {
    super( aTransferId, aRemotePeer, Direction.BOTH );
    myPacketProtocol = aPacketProtocol;
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    myOutputStream = new PacketOutputStream( myRemotePeer, myTransferId, myPacketProtocol );
    myInputStream = new PacketInputStream( myPacketProtocol, myTransferId );
    PacketTransferComposite theTransfer = new PacketTransferComposite();
    theTransfer.addPacketTransfer( myOutputStream );
    theTransfer.addPacketTransfer( myInputStream );
    return theTransfer;
  }
  
  public InputStream getInputStream(){
    return myInputStream;
  }
  
  public OutputStream getOutputStream(){
    return myOutputStream;
  }

  @Override
  public String getTransferDescription() {
    return "stream transfer";
  }

}
