package chabernac.protocol.packet;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophonePacketProvider implements iDataPacketProvider{
  
  private final AudioFormat myAudioFormat;
  private TargetDataLine myDataLine;
  private final int myBufferSize;
  private int myCurrentPacket = 0;
  
  public MicrophonePacketProvider(float aSamplesPerSecond, int aBitSize) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(aSamplesPerSecond, aBitSize, 1, true, true);
    myDataLine = AudioSystem.getTargetDataLine(myAudioFormat);
    myDataLine.open();
    myDataLine.start();
    myBufferSize = myDataLine.getBufferSize() / 5;
  }

  @Override
  public DataPacket getNextPacket() throws IOException {
    byte[] theByte = new byte[myBufferSize];
    myDataLine.read(theByte, 0, myBufferSize);
    DataPacket thePacket = new DataPacket(Integer.toString(myCurrentPacket), theByte);
    System.out.println("Returning packet " + myCurrentPacket);
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
    return myBufferSize;
  }

  @Override
  public void releasePacket(String aPacketId) {
  }

  @Override
  public void close() throws IOException {
    if(myDataLine != null) myDataLine.close();
  }

}
