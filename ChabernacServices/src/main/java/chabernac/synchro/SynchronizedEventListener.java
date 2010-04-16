package chabernac.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.nserver.Client;
import chabernac.nserver.Protocol;
import chabernac.utils.Record;
import chabernac.utils.RecordLoader;

public class SynchronizedEventListener implements iEventListener {
  private static Logger LOGGER = Logger.getLogger(SynchronizedEventListener.class);

  private ClientProtocol myProtocol = null;

  private static SynchronizedEventListener instance = null;

  private String myServer;
  private int myPort;

  private SynchronizedEventListener(){
    ApplicationEventDispatcher.addListener(this, SynchronizedEvent.class);
  }

  private void connect(String aServer, int aPort){
    myServer = aServer;
    myPort = aPort;
    if(myProtocol != null){
      myProtocol.close();
    } else {
      myProtocol = new ClientProtocol();
    }

    Client theClient = new Client(aServer, aPort, myProtocol);
    theClient.start();
  }

  public void eventFired(Event anEvent) {
    if(myProtocol == null) return;

    //reconnect if connection has been closed
    if(myProtocol.isClosed()){
      LOGGER.debug("Reconnecting to server");
      connect(myServer, myPort);
    }

    SynchronizedEvent theEvent = (SynchronizedEvent)anEvent;
    if(!theEvent.isRemoteEvent()){
      myProtocol.writeEvent(theEvent);
    }
  }

  private class ClientProtocol implements Protocol{
    private OutputStream myOutputStream = null;
    private Socket mySocket = null;

    public void handle(Socket aSocket) throws Exception {
      mySocket = aSocket;
      myOutputStream = aSocket.getOutputStream();
      InputStream anInput = aSocket.getInputStream();

      byte[] theBytes = new byte[1024];
      int theByte;
      int i=0;
      long recordLength = -1;
      Record theRecord  = null;
      while((theByte = anInput.read()) != -1){
        theBytes[i++] = (byte)theByte;
        try{
          if(i == 5){
            theRecord = RecordLoader.loadRecord(theBytes);
            recordLength = theRecord.getLength();
          }

          if(i == recordLength){
            i = 0;
            theRecord.setContent(theBytes);


            if(theRecord instanceof iSynchronizableRecord){
              SynchronizedEvent theEvent = ((iSynchronizableRecord)theRecord).getEvent();
              theEvent.setRemoteEvent(true);
              ApplicationEventDispatcher.fireEvent(theEvent);
            }

          }
        }catch(Exception e){
          LOGGER.error("Error occured while creating event", e);
        }

      }

    }

    public void writeEvent(SynchronizedEvent anEvent){
      try{
        myOutputStream.write(anEvent.getRecord().getContent());
        myOutputStream.flush();
      }catch(Exception e){
        LOGGER.error("Could not stream event", e);
      }
    }

    public void close(){
      if(mySocket != null && !mySocket.isClosed()){
        try {
          mySocket.close();
        } catch (IOException e) {
          LOGGER.debug("Could not close socket", e);
        }
      }
    }

    public boolean isClosed(){
      if(mySocket == null){
        return true;
      }

      if(mySocket.isClosed()){
        return true;
      }
      return false;
    }
  }

  public static void connectToServer(String aServer, int aPort){
    if(instance == null){
      instance = new SynchronizedEventListener();
    }
    instance.connect(aServer, aPort);
  }

}
