package chabernac.protocol.packet;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.log4j.Logger;
import org.xiph.speex.SpeexDecoder;

import chabernac.utils.Buffer;

public class MicrophonePacketPersister implements iDataPacketPersister {
  private static final Logger LOGGER = Logger.getLogger(MicrophonePacketPersister.class);
  private final int SPEEX_MODE_WIDEBAND = 1;
  private final int LOWER_LIMIT_BUFFER_TIME = 40; //100 ms under buffer
  private final int UPPER_BUFFER_TIME = 100; //300 ms upper buffer
  private final int DATA_LINE_BUFFER = 50; //50 ms data line buffer
  
  private final AudioFormat myAudioFormat;
  private final SourceDataLine myDataLine;
  private Buffer<DataPacket> mySortedPackets = null;
  private ExecutorService myPlayerThread = null;
  private boolean stop = false;
  private final SpeexDecoder mySpeexDecoder;
  private final int myMaxSpeexBytes;


  public MicrophonePacketPersister(Encoding anEncoding, int aSamplesPerSecond, int aBitSize, int aSpeexQuality, int aPacketsPerSecond) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(anEncoding, aSamplesPerSecond, aBitSize, 1, (aBitSize + 7) / 8, aSamplesPerSecond, false);
    
    float theMSPerPacket = 1000 / (float)aPacketsPerSecond;
    int theLowerLimit = (int)Math.ceil(LOWER_LIMIT_BUFFER_TIME / theMSPerPacket);
    int theUpperLimit = (int)Math.ceil(UPPER_BUFFER_TIME / theMSPerPacket);
    mySortedPackets = new Buffer<DataPacket>(theLowerLimit, theUpperLimit, new DataPacketComparator());
    System.out.println("Buffer: " +  mySortedPackets.getLowerLimit() + " " + mySortedPackets.getUpperLimit());
    
    int theDataLineBufferSize = DATA_LINE_BUFFER * aSamplesPerSecond  * (aBitSize / 2) / 1000;
    
    mySpeexDecoder = new SpeexDecoder();
    mySpeexDecoder.init(SPEEX_MODE_WIDEBAND, aSamplesPerSecond, 1, true);
    myDataLine = (SourceDataLine)AudioSystem.getSourceDataLine(myAudioFormat);
    myDataLine.open(myAudioFormat, theDataLineBufferSize);
    myDataLine.start();
    myMaxSpeexBytes = myDataLine.getBufferSize() / 2;
    System.out.println("data line buffer: " + myDataLine.getBufferSize());
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
    private long myStartTime;
    private int myPacketCounter;
    @Override
    public void run() {
      myStartTime = System.currentTimeMillis();
      while(!stop){
        DataPacket thePacket = mySortedPackets.get();
        byte[] theBytes = thePacket.getBytes();
        myPacketCounter++;
        System.out.println("Playing packet " + thePacket.getId()  + " buffer size: " + mySortedPackets.size() + " bytes " + theBytes.length + " " + (float)(1000 * myPacketCounter) / (float)(System.currentTimeMillis() - myStartTime) + " packets/second");
        try{
          int theCurrentSpeexByte = 0;
          while(theCurrentSpeexByte < theBytes.length){
            mySpeexDecoder.processData(theBytes, theCurrentSpeexByte, myMaxSpeexBytes);
            byte[] theDecodedBytes = new byte[mySpeexDecoder.getProcessedDataByteSize()];
            mySpeexDecoder.getProcessedData(theDecodedBytes, 0);
            myDataLine.write(theDecodedBytes, 0, theDecodedBytes.length);
            theCurrentSpeexByte += myMaxSpeexBytes;
          }
        }catch(StreamCorruptedException e){
          LOGGER.error("Audio packet corrupted", e);
        }
      }
    }
  }
}
