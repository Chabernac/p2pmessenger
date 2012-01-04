package chabernac.protocol.packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import chabernac.utils.Buffer;

public class MicrophonePacketPersister implements iDataPacketPersister {
  private final AudioFormat myAudioFormat;
  private final SourceDataLine myDataLine;
  private Buffer<DataPacket> mySortedPackets = new Buffer<DataPacket>(5,20,new DataPacketComparator());
  private ExecutorService myPlayerThread = null;
  private boolean stop = false;
  
  public MicrophonePacketPersister(Encoding anEncoding, float aSamplesPerSecond, int aBitSize) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(anEncoding, aSamplesPerSecond, aBitSize, 1, (aBitSize + 7) / 8, aSamplesPerSecond, false);
    myDataLine = (SourceDataLine)AudioSystem.getSourceDataLine(myAudioFormat);
    myDataLine.open();
    myDataLine.start();
    myPlayerThread = Executors.newSingleThreadExecutor();
    myPlayerThread.execute(new AudioPlayer());
  }

  @Override
  public void persistDataPacket(DataPacket aPacket) throws IOException {
    mySortedPackets.put(aPacket);
  }

  @Override
  public List<String> listMissingPackets() {
    return null;
  }

  @Override
  public void close() throws IOException {
    stop = true;
    if(myDataLine != null){
      myDataLine.close();
    }
    if(myPlayerThread != null){
      myPlayerThread.shutdownNow();
      myPlayerThread = null;
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
  
  private class AudioPlayer implements Runnable{
    @Override
    public void run() {
      while(!stop){
        DataPacket thePacket = mySortedPackets.get();
        System.out.println("Playing packet " + thePacket.getId()  + " buffer size: " + mySortedPackets.size());
        byte[] theBytes = thePacket.getBytes();
        myDataLine.write(theBytes, 0, theBytes.length);
      }
    }
  }
}
