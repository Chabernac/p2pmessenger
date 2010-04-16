
package chabernac.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import chabernac.log.Logger;

public class Server implements Runnable{
  
  private int port = 123456;
  private boolean stop = false;
  private iProtocol protocol = null;
  
  public Server(int aPort, iProtocol aProtocol){
    port = aPort;
    protocol = aProtocol;
  }
  
  public synchronized void startServer(){
    stop = false;
    new Thread(this).start();
  }
  
  public synchronized void stopServer(){
    stop = true;
  }
  
  public void run(){
    try{
      ServerSocket theSocket = new ServerSocket(port);
      Logger.log(this,"Server listening at port: " +  port);
      while(!stop){
       new SocketHandler(theSocket.accept()).handle();
      }
      theSocket.close();
      Logger.log(this,"Server stopped");
    }catch(IOException e){
      Logger.log(this,"Could not create server socket", e);
    }
  }
  
  private class SocketHandler implements Runnable{
    Socket socket = null;
    
    public SocketHandler(Socket aSocket){
      socket = aSocket;
    }
    
    public void handle(){
      new Thread(this).start();
    }
    
    public void run(){
      Logger.log(this,"Client accepted, using protocol: " + protocol.getClass().getName());
      protocol.handle(socket);
      try {
        socket.close();
      } catch (IOException e) {
        Logger.log(this,"Could not close socket", e);
      }
    }
  }
  
  
  

}
