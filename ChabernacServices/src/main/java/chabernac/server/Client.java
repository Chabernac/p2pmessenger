package chabernac.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class Client implements Runnable{
  private static Logger LOGGER = Logger.getLogger(Class.class);

  private String server = null;
  private int port;
  private Class clientProtocolClass = null;
  private Protocol myProtocol = null;
  private boolean running = false;

  public Client(String server, int port, Protocol aProtocol){
    this.port = port;
    this.server = server;
    this.myProtocol = aProtocol;
  }

  public Client(String server, int port, Class clientProtocolClass){
    this.server = server;
    this.port = port;
    //this.clientProtocol = clientProtocol;
    this.clientProtocolClass = clientProtocolClass;
  }

  public synchronized void run(){
    LOGGER.debug("Starting client to server: " + server + " on port " + port + " ...");
    Socket clientSocket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    Protocol clientProtocol = null;
    try{
      LOGGER.debug("Starting clientprotocol...");
      clientSocket = new Socket(server,port);
      inputStream = clientSocket.getInputStream();
      outputStream = clientSocket.getOutputStream();
      if(myProtocol != null){
        clientProtocol = myProtocol;
      } else {
        clientProtocol = (Protocol)clientProtocolClass.newInstance();
      }
      running = true;
      started();
      clientProtocol.handle(clientSocket.getInputStream(), clientSocket.getOutputStream());
    }catch(Exception e){
      LOGGER.debug("Could net set up client",e);
      e.printStackTrace();
    } finally {
      try {
        if(inputStream != null) { 
          inputStream.close(); 
        }
        if(outputStream != null) {
          outputStream.flush();
          outputStream.close();
        }
        if(clientSocket != null) { 
          clientSocket.close(); 
        }
      }catch(Exception e){LOGGER.debug("Could not close streams",e);}
      running = false;
      notify();
    }

  }

  private synchronized void started(){
    LOGGER.debug("Notifying...");
    notifyAll();
  }



  public synchronized boolean waitTillStarted(){
    try{
      wait();
    }catch(Exception e){LOGGER.debug("Waiting interrupted",e);}
    LOGGER.debug("returning: " + running);
    return running;
  }

  public boolean isRunning(){
    return running;
  }

  public void start()  {
    new Thread(this).start();
  }
}
