
package chabernac.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Server implements Runnable{
  private static Logger logger = Logger.getLogger(Server.class);
  
  private int port = 123456;
  private boolean stop = false;
  private iProtocol protocol = null;
  
  private HashMap sockets = null;
  
  public Server(int aPort, iProtocol aProtocol){
    port = aPort;
    protocol = aProtocol;
    sockets = new HashMap();
  }
  
  public synchronized void startServer(){
    stop = false;
    new Thread(this).start();
  }
  
  public synchronized void stopServer(){
    stop = true;
  }
  
   public SocketDecorator getSocket(String host, int aPort) throws UnknownHostException, IOException{
	 InetAddress theAddress = InetAddress.getByName(host.trim());
    //InetAddress theAddress = InetAddress.getAllByName(host)[0];
    String theKey = theAddress.getHostName();
    if(!sockets.containsKey(theKey) || ((SocketDecorator)sockets.get(theKey)).getSocket().isClosed() ){
      SocketDecorator theSocket = new SocketDecorator(theAddress, aPort);
      sockets.put(theKey, theSocket);
      new SocketHandler(theSocket).handle();
    }
    
    SocketDecorator theSocket = (SocketDecorator)sockets.get(theKey);
    
    return theSocket;
  }
  
  public void run(){
    try{
      ServerSocket theSocket = new ServerSocket(port);
      logger.debug("Server listening at port: " +  port);
      while(!stop){
        SocketDecorator theClientSocket = new SocketDecorator(theSocket.accept());
        String host = theClientSocket.getSocket().getInetAddress().getHostName();
        sockets.put(host, theClientSocket);
        new SocketHandler(theClientSocket).handle();
      }
      theSocket.close();
      logger.debug("Server stopped");
    }catch(IOException e){
      logger.error("Could not create server socket", e);
    }
  }
  
  private class SocketHandler implements Runnable{
    SocketDecorator socket = null;
    
    public SocketHandler(SocketDecorator aSocket){
      socket = aSocket;
    }
    
    public void handle(){
      new Thread(this).start();
    }
    
    public void run(){
      logger.debug("Client accepted, using protocol: " + protocol.getClass().getName());
      try{
    	  protocol.handle(socket);
      }catch(Exception e){
        logger.error("Exception occured in protocol", e);
      }finally{
	      try {
	        socket.close();
	        sockets.remove(socket.getSocket().getInetAddress().getHostAddress());
	      } catch (IOException e) {
          logger.error("Could not close socket", e);
	      }
      }
    }
  }
  
  
  

}
