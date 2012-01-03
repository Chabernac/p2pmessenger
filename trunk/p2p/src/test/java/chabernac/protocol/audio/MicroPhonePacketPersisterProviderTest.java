package chabernac.protocol.audio;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.LineUnavailableException;

import chabernac.protocol.packet.DataPacket;
import chabernac.protocol.packet.MicrophonePacketPersister;
import chabernac.protocol.packet.MicrophonePacketProvider;

public class MicroPhonePacketPersisterProviderTest {

  /**
   * @param args
   * @throws LineUnavailableException 
   */
  public static void main(String[] args) throws LineUnavailableException {
    final MicrophonePacketProvider theProvider = new MicrophonePacketProvider(8000, 8);
    final MicrophonePacketPersister thePersister = new MicrophonePacketPersister(8000, 8);

    final ArrayBlockingQueue<DataPacket> theAudioQueue = new ArrayBlockingQueue<DataPacket>(10);

    new Thread(
        new Runnable(){
          public void run(){
            while(true)
              try {
                theAudioQueue.put(theProvider.getNextPacket());
              } catch (InterruptedException e) {
              } catch (IOException e) {
                e.printStackTrace();
              }
          }
        }).start();
    
    new Thread(
        new Runnable(){
          public void run(){
            while(true)
              try {
                thePersister.persistDataPacket(theAudioQueue.take());
              } catch (IOException e) {
                e.printStackTrace();
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
          }
        }).start();
  }

}
