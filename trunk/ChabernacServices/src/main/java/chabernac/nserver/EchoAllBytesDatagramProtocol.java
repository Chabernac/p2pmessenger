package chabernac.nserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class EchoAllBytesDatagramProtocol implements DatagramProtocol {
  private static Logger LOGGER = Logger.getLogger(EchoAllBytesDatagramProtocol.class);
  private HashSet myParticipants = null;

  public EchoAllBytesDatagramProtocol(){
    myParticipants = new  HashSet();
  }

  public void handle(DatagramSocket aSocket, DatagramPacket aPacket) {
    myParticipants.add(aPacket.getSocketAddress());

    byte[] theData = aPacket.getData();

    //find 0 byte
    int index = -1;
    for(int i=0;i<theData.length;i++){
      if(theData[i] == 0x00){
        index = i;
        break;
      }
    }

    //if the zero byte was found shorten the byte array until the zero byte
    if(index == -1){
      byte[] theNewData = new byte[index + 1];
      System.arraycopy(theData, 0, theNewData, 0, index + 1);
      theData = theNewData;
    }


    //now send the data to all participants.
    //send it at the same port as it was received

    for(Iterator i=myParticipants.iterator();i.hasNext();){
      SocketAddress theAddress = (SocketAddress)i.next();
      //do not send the packet back to where it came from
      if(!theAddress.equals(aPacket.getSocketAddress())){
        //LOGGER.debug("Sending datagram to: " + theAddress + " on port: " + aPacket.getPort() + " data: " + new String(theData));

        try{
          DatagramPacket thePacket = new DatagramPacket(theData, theData.length, theAddress);
          aSocket.send(thePacket);
        }catch(IOException e){
          LOGGER.error("Unable to send datagram packet", e);
        }
      }
    }


  }

}
