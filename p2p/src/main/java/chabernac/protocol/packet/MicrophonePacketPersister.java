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
  private final AudioFormat myAudioFormat;
  private final SourceDataLine myDataLine;
  private Buffer<DataPacket> mySortedPackets = new Buffer<DataPacket>(5,20,new DataPacketComparator());
  private ExecutorService myPlayerThread = null;
  private boolean stop = false;
  private final SpeexDecoder mySpeexDecoder;


  public MicrophonePacketPersister(Encoding anEncoding, int aSamplesPerSecond, int aBitSize, int aSpeexQuality) throws LineUnavailableException{
    myAudioFormat = new AudioFormat(anEncoding, aSamplesPerSecond, aBitSize, 1, (aBitSize + 7) / 8, aSamplesPerSecond, false);
    mySpeexDecoder = new SpeexDecoder();
    mySpeexDecoder.init(SPEEX_MODE_WIDEBAND, aSamplesPerSecond, 1, true);
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

        try{
          mySpeexDecoder.processData(theBytes, 0, theBytes.length);
          byte[] theDecodedBytes = new byte[mySpeexDecoder.getProcessedDataByteSize()];
          mySpeexDecoder.getProcessedData(theDecodedBytes, 0);

          myDataLine.write(theDecodedBytes, 0, theDecodedBytes.length);
        }catch(StreamCorruptedException e){
          LOGGER.error("Audio packet corrupted", e);
        }
      }
    }
  }
}
