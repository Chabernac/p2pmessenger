package chabernac.protocol.packet;

import java.io.IOException;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.LineUnavailableException;

public class AudioTransferState extends AbstractTransferState {
  private final PacketProtocol myPacketProtocol;
  private final int mySamplesPerSecond;
  private final int myBits;
  private final Encoding myEncoding;
  private final int mySpeexQuality;

  public AudioTransferState(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Direction aDirection, Encoding anEncoding, int aSamplesPerSecond, int aBits, int aSpeexQuality) {
    super(aTransferId, aRemotePeer, aDirection);
    myEncoding = anEncoding;
    myPacketProtocol = aPacketProtocol;
    mySamplesPerSecond = aSamplesPerSecond;
    myBits = aBits;
    mySpeexQuality = aSpeexQuality;
  }
  
  public static AudioTransferState createForReceive(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Encoding anEncoding, int aSamplesPerSeconds, int aBits, int aSpeexQuality){
    return new AudioTransferState(aPacketProtocol, aTransferId, aRemotePeer, Direction.SEND, anEncoding, aSamplesPerSeconds, aBits, aSpeexQuality);
  }
  
  public static AudioTransferState createForSend(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Encoding anEncoding, int aSamplesPerSecond, int aBits, int aSpeexQuality){
    return new AudioTransferState(aPacketProtocol, aTransferId, aRemotePeer, Direction.RECEIVE, anEncoding, aSamplesPerSecond, aBits, aSpeexQuality);
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    try{
      if(myDirection == Direction.RECEIVE){
        return new PacketReceiver(myPacketProtocol, myTransferId, new MicrophonePacketPersister(myEncoding, mySamplesPerSecond, myBits, mySpeexQuality));
      } else {
        return new AsyncPacketSender(myPacketProtocol, myTransferId, myRemotePeer, new MicrophonePacketProvider(myEncoding, mySamplesPerSecond, myBits, mySpeexQuality));
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
