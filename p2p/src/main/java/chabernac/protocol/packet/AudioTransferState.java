package chabernac.protocol.packet;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

public class AudioTransferState extends AbstractTransferState {
  private final PacketProtocol myPacketProtocol;
  private final int mySamplesPerSecond;
  private final int myBits;

  public AudioTransferState(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Direction aDirection, int aSamplesPerSecond, int aBits) {
    super(aTransferId, aRemotePeer, aDirection);
    myPacketProtocol = aPacketProtocol;
    mySamplesPerSecond = aSamplesPerSecond;
    myBits = aBits;
  }
  
  public static AudioTransferState createForReceive(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, int aSamplesPerSeconds, int aBits){
    return new AudioTransferState(aPacketProtocol, aTransferId, aRemotePeer, Direction.SEND, aSamplesPerSeconds, aBits);
  }
  
  public static AudioTransferState createForSend(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, int aSamplesPerSecond, int aBits){
    return new AudioTransferState(aPacketProtocol, aTransferId, aRemotePeer, Direction.RECEIVE, aSamplesPerSecond, aBits);
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    try{
      if(myDirection == Direction.RECEIVE){
        return new PacketReceiver(myPacketProtocol, myTransferId, new MicrophonePacketPersister(mySamplesPerSecond, myBits));
      } else {
        return new AsyncPacketSender(myPacketProtocol, myTransferId, myRemotePeer, new MicrophonePacketProvider(mySamplesPerSecond, myBits));
      }
    }catch(LineUnavailableException e){
      throw new IOException("Line not available", e);
    }
  }

  @Override
  public String getTransferDescription() {
    if(myDirection == Direction.RECEIVE) return "Receiving audio";
    else if(myDirection == Direction.SEND) return "Sending audio";
    return null;
  }

}
