package chabernac.protocol.audio;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioTest {

  /**
   * @param args
   * @throws LineUnavailableException 
   */
  public static void main(String[] args) throws LineUnavailableException {
    Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
    for (int i=0;i<mixerInfos.length;i++){
      Mixer.Info theMixerInfo = mixerInfos[i];
      Mixer m = AudioSystem.getMixer(theMixerInfo);
      System.out.println("Mixer[" + i + "]" + theMixerInfo.getName());
      Line.Info[] lineInfos = m.getSourceLineInfo();
      for (Line.Info lineInfo:lineInfos){
        System.out.println ("Mixer[" + i + "]" + theMixerInfo.getName()+"---"+lineInfo);
        Line line = m.getLine(lineInfo);
        System.out.println("\t-----"+line);
      }
      lineInfos = m.getTargetLineInfo();
      for (Line.Info lineInfo:lineInfos){
        System.out.println ("Mixer[" + i + "]---" + lineInfo);
        Line line = m.getLine(lineInfo);
        System.out.println("\t-----"+line);

      }

    }

    System.out.println("line in " + AudioSystem.isLineSupported(Port.Info.LINE_IN));
    System.out.println("CD " + AudioSystem.isLineSupported(Port.Info.COMPACT_DISC));
    System.out.println("headphone " + AudioSystem.isLineSupported(Port.Info.HEADPHONE));
    System.out.println("line out " + AudioSystem.isLineSupported(Port.Info.LINE_OUT));
    System.out.println("microphone " + AudioSystem.isLineSupported(Port.Info.MICROPHONE));
    System.out.println("speaker " + AudioSystem.isLineSupported(Port.Info.SPEAKER));

//    TargetDataLine theLine = (TargetDataLine)AudioSystem.getLine(Port.Info.MICROPHONE);
//    theLine.open();
    
    AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    TargetDataLine theLine = AudioSystem.getTargetDataLine(format);
    ByteArrayOutputStream out  = new ByteArrayOutputStream();
    int numBytesRead;
    System.out.println("buffer size: '" + theLine.getBufferSize() +  "'");
    byte[] data = new byte[theLine.getBufferSize() / 5];
    System.out.println("byte size: '" + data.length +  "'");

    // Begin audio capture.
    theLine.open();
    theLine.start();

    // Here, stopped is a global boolean set by another thread.
    for(int i=0;i<100;i++){
      // Read the next chunk of data from the TargetDataLine.
      numBytesRead =  theLine.read(data, 0, data.length);
      System.out.println("Bytes read '" + numBytesRead + "'");
      // Save this chunk of data.
      out.write(data, 0, numBytesRead);
      System.out.println("saving sample, first byte: " + data[10]);
    }

    SourceDataLine theSpeaker = (SourceDataLine)AudioSystem.getSourceDataLine(format);
    theSpeaker.open();
    theSpeaker.start();

    byte[] theAudio = out.toByteArray();


    int length = theSpeaker.getBufferSize() / 5;
    int theBytesWritten = 0;

    while(theBytesWritten < theAudio.length){
      int theLenght = theAudio.length - theBytesWritten > length ? length : theAudio.length - theBytesWritten;
      System.out.println("Writing sample start " + theBytesWritten + " length " + theLenght);
      theSpeaker.write(theAudio, theBytesWritten, theLenght);
      theBytesWritten += length;
    }
  }

}
