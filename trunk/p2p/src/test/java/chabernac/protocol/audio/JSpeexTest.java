package chabernac.protocol.audio;

import org.xiph.speex.SpeexEncoder;

public class JSpeexTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    SpeexEncoder theEncoder = new SpeexEncoder();
    theEncoder.init(1, 8, 8000, 1);

    int complexity = 3;
    int bitrate =-1;
    float vbr_quality = -1;
    boolean vbr = false;
    boolean vad = false;
    boolean dtx = false;

    if (complexity > 0) 
    {
      theEncoder.getEncoder().setComplexity(complexity);
    }
    if (bitrate > 0) 
    {
      theEncoder.getEncoder().setBitRate(bitrate);
    }
    if (vbr) 
    {
      theEncoder.getEncoder().setVbr(vbr);
      if (vbr_quality > 0) 
      {
        theEncoder.getEncoder().setVbrQuality(vbr_quality);
      }
    }
    if (vad) 
    {
      theEncoder.getEncoder().setVad(vad);
    }
    if (dtx) 
    {
      theEncoder.getEncoder().setDtx(dtx);
    }

    byte[] theBytes = new byte[640];
    theEncoder.processData(theBytes, 0, theBytes.length);
  }

}
