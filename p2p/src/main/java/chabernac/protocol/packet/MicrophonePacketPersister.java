package chabernac.protocol.packet;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.xiph.speex.SpeexDecoder;

import chabernac.utils.Buffer;

public class MicrophonePacketPersister implements iDataPacketPersister {
  private static final Logger LOGGER = Logger.getLogger(MicrophonePacketPersister.class);
  private final int SPEEX_MODE_WIDEBAND = 1;
  private final int LOWER_LIMIT_BUFFER_TIME = 40; //100 ms under buffer
  private final int UPPER_BUFFER_TIME = 100; //300 ms upper buffer
  private final int DATA_LINE_BUFFER = 20; //50 ms data line buffer

  private final AudioFormat myAudioFormat;
  private final SourceDataLine myDataLine;
  private Buffer<DataPacket> mySortedPackets = null;
  private ExecutorService myPlayerThread = null;
  private boolean stop = false;
  private final SpeexDecoder mySpeexDecoder;


  public MicrophonePacketPersister(Encoding anEncoding, int aSamplesPerSecond, int aBitSize, int aPacketsPerSecond) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(anEncoding, aSamplesPerSecond, aBitSize, 1, (aBitSize + 7) / 8, aSamplesPerSecond, false);

    float theMSPerPacket = 1000 / (float)aPacketsPerSecond;
    int theLowerLimit = (int)Math.ceil(LOWER_LIMIT_BUFFER_TIME / theMSPerPacket);
    int theUpperLimit = (int)Math.ceil(UPPER_BUFFER_TIME / theMSPerPacket);
    mySortedPackets = new Buffer<DataPacket>(theLowerLimit, theUpperLimit, new DataPacketComparator());
    System.out.println("Buffer: " +  mySortedPackets.getLowerLimit() + " " + mySortedPackets.getUpperLimit());

    int theDataLineBufferSize =  DATA_LINE_BUFFER * aSamplesPerSecond  * (aBitSize / 2) / 1000;
    System.out.println("Data line buffer " + theDataLineBufferSize);
    //    int theDataLineBufferSize = 4000;

    mySpeexDecoder = new SpeexDecoder();
    mySpeexDecoder.init(SPEEX_MODE_WIDEBAND, aSamplesPerSecond, 1, true);
    myDataLine = (SourceDataLine)AudioSystem.getSourceDataLine(myAudioFormat);
    myDataLine.open(myAudioFormat, theDataLineBufferSize);
    myDataLine.start();
    System.out.println("data line buffer: " + myDataLine.getBufferSize());
    myPlayerThread = Executors.newSingleThreadExecutor();
    myPlayerThread.execute(new AudioPlayer());
  }

  @Override
  public void persistDataPacket(DataPacket aPacket) throws IOException {
//    mySortedPackets.put(aPacket);
    playPacket(aPacket);
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

  private void playPacket(DataPacket aPacket){
    byte[] theBytes = aPacket.getBytes();

    //the first byte is the number of speech packets
    int theSpeechPacketLength = (theBytes.length - 1) / theBytes[0];
    try{
      int theCurrentSpeexByte = 1;
      while(theCurrentSpeexByte < theBytes.length){
        mySpeexDecoder.processData(theBytes, theCurrentSpeexByte, theSpeechPacketLength);
        int theProcessedBytes = mySpeexDecoder.getProcessedDataByteSize();
        byte[] theDecodedBytes = new byte[theProcessedBytes];
        mySpeexDecoder.getProcessedData(theDecodedBytes, 0);
        myDataLine.write(theDecodedBytes, 0, theDecodedBytes.length);
        theCurrentSpeexByte += theSpeechPacketLength;
      }
    }catch(StreamCorruptedException e){
      LOGGER.error("Audio packet corrupted", e);
    }
  }

  private class AudioPlayer implements Runnable{
    private long myStartTime;
    private int myPacketCounter;
    @Override
    public void run() {
      myStartTime = System.currentTimeMillis();
      while(!stop){
        myPacketCounter++;
        DataPacket thePacket = mySortedPackets.get();
        System.out.println("Playing packet " + thePacket.getId()  + " buffer size: " + mySortedPackets.size() +  (float)(1000 * myPacketCounter) / (float)(System.currentTimeMillis() - myStartTime) + " packets/second");
        playPacket(thePacket);
      }
    }
  }
}
