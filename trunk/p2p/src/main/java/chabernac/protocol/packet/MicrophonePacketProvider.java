package chabernac.protocol.packet;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.xiph.speex.SpeexEncoder;

public class MicrophonePacketProvider implements iDataPacketProvider{
  private final int SPEEX_MODE_WIDEBAND = 1;
  
  private final AudioFormat myAudioFormat;
  private TargetDataLine myDataLine;
  private int myCurrentPacket = 0;
  private final int myMaxBytes;
  private final SpeexEncoder mySpeexEncoder;
  private final int myMaxSpeexBytes;
  
  public MicrophonePacketProvider(Encoding anEncoding, int aSamplesPerSecond, int aBitSize, int aSpeexQuality, int aFramesPerSecond) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(anEncoding, aSamplesPerSecond, aBitSize, 1, (aBitSize + 7) / 8, aSamplesPerSecond, false);
    mySpeexEncoder = new SpeexEncoder();
    mySpeexEncoder.init(SPEEX_MODE_WIDEBAND, aSpeexQuality, aSamplesPerSecond, 1);
    myDataLine = AudioSystem.getTargetDataLine(myAudioFormat);
    myDataLine.open();
    myDataLine.start();
    myMaxSpeexBytes = mySpeexEncoder.getFrameSize() * 2;
    int theMaxBytes = aSamplesPerSecond * (aBitSize / 8) / aFramesPerSecond;
    //myMaxBytes should be a multiple of my max speex bytes
    int theMultiply = Math.round((float)theMaxBytes / (float)myMaxSpeexBytes);
    if(theMultiply <= 0) theMultiply = 1;
    myMaxBytes = theMultiply * myMaxSpeexBytes;
  }

  @Override
  public DataPacket getNextPacket() throws IOException {
    byte[] theByte = new byte[myMaxBytes];
//    System.out.println("packet size " + theByte.length + " " + mySpeexEncoder.getEncoder().getFrameSize());
    System.out.println("packet size " + theByte.length);
    myDataLine.read(theByte, 0, theByte.length);

    byte[] theProcessedBytes = new byte[myMaxBytes];
    
    int theCurrentByte = 0;
    int theCurrentSpeexByte = 0;
    int theNrOfBytesToRead = theByte.length < myMaxSpeexBytes ? theByte.length : myMaxSpeexBytes; 
    while(theCurrentByte < myMaxBytes){
      int theRemainingBytes = theByte.length - theCurrentByte;
      int theBytesToRead = theRemainingBytes < theNrOfBytesToRead ? theRemainingBytes : theNrOfBytesToRead;
      mySpeexEncoder.processData(theByte, theCurrentByte, theBytesToRead);
      int theProcessedDataByteSize = mySpeexEncoder.getProcessedDataByteSize();
      mySpeexEncoder.getProcessedData(theProcessedBytes, theCurrentSpeexByte);
      theCurrentSpeexByte += theProcessedDataByteSize;
      theCurrentByte += theBytesToRead;
    }
    
    //now trim the byte array
    byte[] theEncodedBytes = new byte[theCurrentSpeexByte];
    
    System.arraycopy( theProcessedBytes, 0, theEncodedBytes, 0, theCurrentSpeexByte );
    
    System.out.println("Speex reduced packet size from '" + theByte.length + "' to " + theEncodedBytes.length  + "' " + (100 * (float)theEncodedBytes.length / (float)theByte.length) + " % compression");
    System.out.println("returning packet " + myCurrentPacket);
    DataPacket thePacket = new DataPacket(Integer.toString(myCurrentPacket), theEncodedBytes);
    
    myCurrentPacket++;
    return thePacket;
  }

  @Override
  public boolean hasNextPacket() {
    return true;
  }

  @Override
  public DataPacket getPacket(String aPacketId) throws IOException {
    throw new IOException("Not possible to obtain a previous packet");
  }

  @Override
  public int getNrOfPackets() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int getPacketSize() {
    return myMaxBytes;
  }

  @Override
  public void releasePacket(String aPacketId) {
  }

  @Override
  public void close() throws IOException {
    if(myDataLine != null) myDataLine.close();
  }

}
