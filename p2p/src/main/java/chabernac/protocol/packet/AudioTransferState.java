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
  private final int myPacketsPerSecond;

  public AudioTransferState(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Encoding anEncoding, int aSamplesPerSecond, int aBits, int aSpeexQuality, int aPacketsPerSecond) {
    super(aTransferId, aRemotePeer, Direction.BOTH);
    myEncoding = anEncoding;
    myPacketProtocol = aPacketProtocol;
    mySamplesPerSecond = aSamplesPerSecond;
    myBits = aBits;
    mySpeexQuality = aSpeexQuality;
    myPacketsPerSecond = aPacketsPerSecond;
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    try{
      PacketTransferComposite theAudioComposite = new PacketTransferComposite();
      theAudioComposite.addPacketTransfer( new PacketReceiver(myPacketProtocol, myTransferId, new MicrophonePacketPersister(myEncoding, mySamplesPerSecond, myBits, myPacketsPerSecond)) );
      theAudioComposite.addPacketTransfer( new AsyncPacketSender(myPacketProtocol, myTransferId, myRemotePeer, new MicrophonePacketProvider(myEncoding, mySamplesPerSecond, myBits, mySpeexQuality, myPacketsPerSecond)) );
      return theAudioComposite;
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
