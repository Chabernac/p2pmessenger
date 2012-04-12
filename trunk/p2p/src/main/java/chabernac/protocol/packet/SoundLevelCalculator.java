package chabernac.protocol.packet;

import javax.sound.sampled.AudioFileFormat;

public class SoundLevelCalculator {
  private final AudioFileFormat myAudioFormat;

  public SoundLevelCalculator(AudioFileFormat aAudioFormat) {
    super();
    myAudioFormat = aAudioFormat;
  }
  
  public float calculateLevel(byte[] anAudioSamples){
    myAudioFormat.get
  }
}
