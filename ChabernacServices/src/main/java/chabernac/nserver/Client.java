package chabernac.nserver;

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
    Protocol clientProtocol = null;
    try{
      LOGGER.debug("Starting clientprotocol...");
      clientSocket = new Socket(server,port);
      if(myProtocol != null){
        clientProtocol = myProtocol;
      } else {
        clientProtocol = (Protocol)clientProtocolClass.newInstance();
      }
      running = true;
      started();
      clientProtocol.handle(clientSocket);
      LOGGER.debug("Ended client to server: " + server + " on port " + port + " ...");
    }catch(Exception e){
      LOGGER.debug("Could net set up client",e);
    } finally {
      try {
        if(clientSocket != null && !clientSocket.isClosed()) { 
          clientSocket.close(); 
        }
      }catch(Exception e){
        LOGGER.debug("Could not close streams",e);
      }
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
