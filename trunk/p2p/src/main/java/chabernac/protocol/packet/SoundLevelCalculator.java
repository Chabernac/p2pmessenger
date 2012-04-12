package chabernac.protocol.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class SoundLevelCalculator {
  private final AudioFormat myAudioFormat;
  private final int myBytesPerFrame;
  private final boolean isBigEndian;
  private iLevelCalculator myLevelCalculator;
  private final ByteBuffer myByteBuffer;

  public SoundLevelCalculator(AudioFormat aAudioFormat) {
    super();
    myAudioFormat = aAudioFormat;
    myBytesPerFrame = myAudioFormat.getSampleSizeInBits() / 8;
    isBigEndian = myAudioFormat.isBigEndian();
    myByteBuffer = ByteBuffer.allocate( myBytesPerFrame );
    initLevelCalculator();
  }
  
  private void initLevelCalculator(){
    if(myAudioFormat.getEncoding().equals( Encoding.PCM_SIGNED)){
      myLevelCalculator = new PCMSignedLevelCalculator();
    } else {
      throw new IllegalArgumentException("Only supporting PCM signed level calculation at the moment");
    }
  }
  
  public double calculateLevel(byte[] anAudioSamples){
    return myLevelCalculator.calculateLevel( anAudioSamples );
  }
  
  private interface iLevelCalculator{
    public double calculateLevel(byte[] aBytes);
  }
  
  private class PCMSignedLevelCalculator implements iLevelCalculator{
    @Override
    public double calculateLevel( byte[] anAudioSamples) {
      ShortBuffer theBuffer = ByteBuffer.wrap( anAudioSamples ).order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN).asShortBuffer();
      short[] theAudioShorts = new short[theBuffer.capacity()];
      theBuffer.get(theAudioShorts);
      
      int theLength = theAudioShorts.length / 4;
      double theAverageLevel = 0;
      for(int i=0;i<theLength;i++){
        theAverageLevel += Math.pow( theAudioShorts[i], 2);
      }
      theAverageLevel /= theLength;
      theAverageLevel = Math.sqrt( theAverageLevel );
      
      return theAverageLevel;
    }
  }
}
