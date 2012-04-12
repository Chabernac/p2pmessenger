package chabernac.protocol.packet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import chabernac.protocol.message.FailedMessageResender.MyDeliveryReportListener;

public class SoundLevelCalculator {
  private final AudioFormat myAudioFormat;
  private final int myBytesPerFrame;
  private final boolean isBigEndian;
  private final Encoding

  public SoundLevelCalculator(AudioFormat aAudioFormat) {
    super();
    myAudioFormat = aAudioFormat;
    myBytesPerFrame = myAudioFormat.getSampleSizeInBits() / 8;
    isBigEndian = myAudioFormat.isBigEndian();
    aAudioFormat.getEncoding();
  }
  
  public float calculateLevel(byte[] anAudioSamples){
    int theFrames = anAudioSamples.length / myBytesPerFrame;
    
    double theLevel = 0;
    for(int i=0;i<theFrames;i++){
      theLevel += calculateLevel(anAudioSamples, i);
    }
  }
  
  private float calculateLevel(byte[] anAudioSamples, int aFrame){
    int thePosition = aFrame * myBytesPerFrame;
    
    
  }
}
