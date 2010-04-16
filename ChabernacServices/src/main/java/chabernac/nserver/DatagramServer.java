package chabernac.nserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class DatagramServer implements Runnable{
  private static Logger LOGGER = Logger.getLogger(DatagramServer.class);
  private int myPort;
  private int myMaxPacketLength = 1024;
  private boolean isRunning = false;
  private DatagramSocket mySocket = null;
  private DatagramProtocol myProtocol = null;
  
  public DatagramServer(int aPort, DatagramProtocol aProtocol){
    this(aPort, 1024, aProtocol);
  }
  
  public DatagramServer(int aPort, int aMaxPacketLength, DatagramProtocol aProtocol){
    myPort = aPort;
    myMaxPacketLength = aMaxPacketLength;
    myProtocol = aProtocol;
  }
  
  public void run(){
    //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    try{
      mySocket = new DatagramSocket(myPort);
      byte[] theBuffer = new byte[myMaxPacketLength];
      final DatagramPacket thePacket = new DatagramPacket(theBuffer, theBuffer.length);
      LOGGER.debug("Datagram server started on port: " + myPort);
      while(true){
        mySocket.receive(thePacket);
        //LOGGER.debug("Datagram packet received from: " + thePacket.getAddress() + " on port: " + thePacket.getPort() + " data: " + new String(thePacket.getData()));
//        new Thread(new Runnable(){
//          public void run(){
            myProtocol.handle(mySocket, thePacket);
//          }
//        }).start();
      }
    }catch(SocketException e){
      LOGGER.error("An error occured in datagram socket", e);
    } catch (IOException e) {
      LOGGER.error("An error while receiving datagram packet", e);
    }
    LOGGER.debug("Datagram server stoped");
    isRunning = false;
  }
  
  public void send(DatagramPacket aPacket){
    if(mySocket != null){
      try {
        mySocket.send(aPacket);
      } catch (IOException e) {
        LOGGER.error("An error occured while sending packet", e);
      }
    }
  }
  
  public void start(){
    if(!isRunning){
      isRunning = true;
      new Thread(this).start();
    }
  }
  
  public void stop(){
    isRunning = false;
    mySocket.close();
  }
  
  public boolean isRunning(){
    return isRunning;
  }
  
  

  public int getPort() {
    return myPort;
  }

  public void setPort(int anPort) {
    myPort = anPort;
  }
  
  

}
