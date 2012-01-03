package chabernac.protocol.packet;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MicrophonePacketPersister implements iDataPacketPersister {
  private final AudioFormat myAudioFormat;
  private final SourceDataLine myDataLine;
  private TreeSet<DataPacket> mySortedPackets = new TreeSet<DataPacket>(new DataPacketComparator());
  private int myNrOfStoredPackets = 10;
  
  public MicrophonePacketPersister(float aSamplesPerSecond, int aBitSize) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(aSamplesPerSecond, aBitSize, 1, true, true);
    myDataLine = (SourceDataLine)AudioSystem.getSourceDataLine(myAudioFormat);
    myDataLine.open();
    myDataLine.start();
  }

  @Override
  public void persistDataPacket(DataPacket aPacket) throws IOException {
    System.out.println("adding audio packet "  + aPacket.getId());
    mySortedPackets.add(aPacket);
    while(mySortedPackets.size() > myNrOfStoredPackets){
      byte[] theBytes = mySortedPackets.pollFirst().getBytes();
      System.out.println(theBytes[10]);
      myDataLine.write(theBytes, 0, theBytes.length);
    }
  }

  @Override
  public List<String> listMissingPackets() {
    return null;
  }

  @Override
  public void close() throws IOException {
    if(myDataLine != null){
      myDataLine.close();
    }
  }

  @Override
  public int getNrOfPackets() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean isComplete() {
    return false;
  }

}
