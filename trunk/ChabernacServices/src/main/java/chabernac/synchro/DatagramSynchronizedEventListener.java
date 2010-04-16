package chabernac.synchro;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.nserver.DatagramProtocol;
import chabernac.nserver.DatagramServer;
import chabernac.utils.NetTools;
import chabernac.utils.Record;
import chabernac.utils.RecordLoader;

public class DatagramSynchronizedEventListener implements iEventListener {
  private static Logger LOGGER = Logger.getLogger(DatagramSynchronizedEventListener.class);

  private static DatagramSynchronizedEventListener instance = null;

  private String myServer;
  private int myPort;
  private DatagramServer myDServer = null;

  private DatagramSynchronizedEventListener(){
    ApplicationEventDispatcher.addListener(this, SynchronizedEvent.class);
  }

  private void connect(String aServer, int aPort){
    myServer = aServer;
    myPort = aPort;
    if(myDServer != null){
      myDServer.stop();
    }
    try{
      myDServer = new DatagramServer(NetTools.findUnusedLocalPort(), new MyDatagramProtocol());
      myDServer.start();
    }catch(Exception e){
      LOGGER.error("Unable to start local datagram server", e);
    }
  }

  public void eventFired(Event anEvent) {
    SynchronizedEvent theEvent = (SynchronizedEvent)anEvent;
    if(!theEvent.isRemoteEvent()){
      byte[] theData = theEvent.getRecord().getContent();
      try{
        DatagramPacket thePacket = new DatagramPacket(theData, theData.length, InetAddress.getByName(myServer), myPort);
        if(myDServer != null){
          myDServer.send(thePacket);
        }
      }catch(UnknownHostException e){
        LOGGER.error("Unknown host", e);
      }
    }
  }
    


  public static void connectToServer(String aServer, int aPort){
    if(instance == null){
      instance = new DatagramSynchronizedEventListener();
    }
    instance.connect(aServer, aPort);
  }
  
  private class MyDatagramProtocol implements DatagramProtocol{

    public void handle(DatagramSocket aSocket, DatagramPacket aPacket) {
      Record theRecord = RecordLoader.loadRecord(aPacket.getData());
      if(theRecord != null && theRecord instanceof SynchronizedRecord){
        SynchronizedEvent theEvent = ((SynchronizedRecord)theRecord).getEvent();
        theEvent.setRemoteEvent(true);
        ApplicationEventDispatcher.fireEvent(theEvent);
      }
    }
    
  }

}
