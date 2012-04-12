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
  private iSoundLevelTreshHoldProvider mySoundLevelTreshHoldProvider = null;

  public AudioTransferState(PacketProtocol aPacketProtocol, String aTransferId, String aRemotePeer, Encoding anEncoding, int aSamplesPerSecond, int aBits, int aSpeexQuality, int aPacketsPerSecond, Side aSide, Direction aDirection) {
    super(aTransferId, aRemotePeer, aSide, aDirection);
    myEncoding = anEncoding;
    myPacketProtocol = aPacketProtocol;
    mySamplesPerSecond = aSamplesPerSecond;
    myBits = aBits;
    mySpeexQuality = aSpeexQuality;
    myPacketsPerSecond = aPacketsPerSecond;
    
    //remove
    SoundLevelFrame theFrame = new SoundLevelFrame();
    theFrame.setVisible( true );
    mySoundLevelTreshHoldProvider = theFrame.getSoundLevelThreshHoldProvider(); 
  }
  
  public void setSoundLevelThreshHoldProvider(iSoundLevelTreshHoldProvider aSoundLevelThreshHoldProvider){
    mySoundLevelTreshHoldProvider = aSoundLevelThreshHoldProvider;
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException {
    try{
      PacketTransferComposite theAudioComposite = new PacketTransferComposite();
      if(myDirection == Direction.BOTH || myDirection == Direction.RECEIVE){
        theAudioComposite.addPacketTransfer( new PacketReceiver(myPacketProtocol, myTransferId, new MicrophonePacketPersister(myEncoding, mySamplesPerSecond, myBits, myPacketsPerSecond, mySoundLevelTreshHoldProvider)) );
      }
      
      if(myDirection == Direction.BOTH || myDirection == Direction.SEND){
        theAudioComposite.addPacketTransfer( new AsyncPacketSender(myPacketProtocol, myTransferId, myRemotePeer, new MicrophonePacketProvider(myEncoding, mySamplesPerSecond, myBits, mySpeexQuality, myPacketsPerSecond, mySoundLevelTreshHoldProvider)) );
      }
      
      return theAudioComposite;
    }catch(LineUnavailableException e){
      throw new IOException("Line not available", e);
    }
  }

  @Override
  public String getTransferDescription() {
    if(myDirection == Direction.RECEIVE) return "Receiving audio";
    else if(myDirection == Direction.SEND) return "Sending audio";
    else if(myDirection == Direction.BOTH) return "Send/Receive audio";
    return null;
  }
}
