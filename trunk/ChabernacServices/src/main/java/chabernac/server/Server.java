package chabernac.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class Server implements Runnable{
  private static Logger LOGGER = Logger.getLogger(Server.class);

  private int port;
  private Constructor serverProtocolConstructor = null;
  private Object[] initArgs = null;
  private boolean running = false;
  private Protocol myProtocol = null;
  private ServerSocket myServerSocket = null;

  public Server(int port, Protocol aProtocol)  {
    this.port = port;
    this.myProtocol = aProtocol;
  }

  public Server(int port, Constructor serverProtocolConstructor, Object[] initArgs) throws InvalidProtocolException  {
    this.port = port;
    this.serverProtocolConstructor = serverProtocolConstructor;
    this.initArgs = initArgs;
  }

  public Server(int port, Class serverProtocol) throws InvalidProtocolException, NoSuchMethodException  {
    this(port, serverProtocol.getConstructor(null),null);
  }

  public void run()  {
    LOGGER.debug("Starting server on port " + port + " ...");
    System.out.println("Starting server on port " + port + " ...");
    running = true;
    try{
      myServerSocket = new ServerSocket(port);
      while(!myServerSocket.isClosed()){
        LOGGER.debug("Waiting for client socket...");
        new Thread(new SocketHandler(myServerSocket.accept())).start();
      }
    }catch(Exception e){ 
      LOGGER.debug("Error occured in server thread",e); 
    }
    finally{
      try{
        if(myServerSocket != null){ 
          myServerSocket.close(); 
        }
      }catch(Exception e){
        LOGGER.debug("Could not close serverSocket",e);
      }
      running = false;
    }

  }

  public void stop()  {
    try{
      myServerSocket.close();
    }catch(Exception e){
      LOGGER.debug("Could not stop server",e);
    }
  }

  public void start(){
    new Thread(this).start();
  }

  public boolean isRunning(){
    return running;
  }

  private class SocketHandler implements Runnable  {
    private Socket socket = null;

    public SocketHandler(Socket socket){
      LOGGER.debug("Client socket accepted");
      System.out.println("Client socket accepted");
      this.socket = socket;
    }

    public void run(){
      LOGGER.debug("Running server protocol...");
      InputStream inputStream = null;
      OutputStream outputStream = null;
      Protocol serverProtocol = null;
      try{
        if(myProtocol != null){
          serverProtocol = myProtocol;
        } else {
          serverProtocol = (Protocol)serverProtocolConstructor.newInstance(initArgs);
        }
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        serverProtocol.handle(inputStream,outputStream);
      } catch(Exception e){
    	  LOGGER.debug("Exception occured in protocol: " ,e);
      }
      finally{
        try{
          inputStream.close();
          outputStream.flush();
          outputStream.close();
          socket.close();
        }catch(Exception e){
          LOGGER.debug("Could not close streams",e);
        }
      }
    }
  }


}
