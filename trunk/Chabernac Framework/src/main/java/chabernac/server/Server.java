package chabernac.server;

import java.net.*;
import chabernac.utils.Debug;
import java.lang.reflect.*;
import java.io.*;

public class Server implements Runnable
{
  private int port;
  private Constructor serverProtocolConstructor = null;
  private Object[] initArgs = null;
  private boolean stop = false;
  private boolean running = false;
  private Protocol myProtocol = null;
  private ServerSocket myServerSocket = null;

  public Server(int port, Protocol aProtocol)
  {
	this.port = port;
	this.myProtocol = aProtocol;
  }

  public Server(int port, Constructor serverProtocolConstructor, Object[] initArgs) throws InvalidProtocolException
  {
	Class declaringClass = serverProtocolConstructor.getDeclaringClass();
	this.port = port;
    this.serverProtocolConstructor = serverProtocolConstructor;
    this.initArgs = initArgs;
  }

  public Server(int port, Class serverProtocol) throws InvalidProtocolException, NoSuchMethodException
  {
	  this(port, serverProtocol.getConstructor(null),null);
  }

  public void run()
  {
   Debug.log(this,"Starting server on port " + port + " ...");
   running = true;
   try{
      myServerSocket = new ServerSocket(port);
      while(!myServerSocket.isClosed()){
        Debug.log(this,"Waiting for client socket...");
        new Thread(new SocketHandler(myServerSocket.accept())).start();
      }
    }catch(Exception e){ Debug.log(this,"Error occured in server thread",e); }
     finally{
       try{
          if(myServerSocket != null){ myServerSocket.close(); }
       }catch(Exception e){
          Debug.log(this,"Could not close serverSocket",e);
       }
       running = false;
     }

  }

  public void stop()
  {
      try{
        myServerSocket.close();
      }catch(Exception e){
          Debug.log(this,"Could not stop server",e);
      }
  }

  public void start()
  {
    new Thread(this).start();
  }

  public boolean isRunning(){return running;}

  private class SocketHandler implements Runnable
  {
    private Socket socket = null;

    public SocketHandler(Socket socket)
    {
	  Debug.log(this,"Client socket accepted");
      this.socket = socket;
    }

    public void run()
    {
	  Debug.log(this,"Running server protocol...");
	  InputStream inputStream = null;
	  OutputStream outputStream = null;
	  Protocol serverProtocol = null;
	  try
	  {
		  if(myProtocol != null){
			  serverProtocol = myProtocol;
		  } else {
		  	serverProtocol = (Protocol)serverProtocolConstructor.newInstance(initArgs);
		  }
		  inputStream = socket.getInputStream();
		  outputStream = socket.getOutputStream();
		  serverProtocol.handle(inputStream,outputStream);
 	  }catch(Exception e){Debug.log(this,"Exception occured in protocol: " + serverProtocolConstructor.toString(),e);}
 	   finally
 	   {
		   try
		   {
		   inputStream.close();
		   outputStream.flush();
		   outputStream.close();
		   socket.close();
	   	   }catch(Exception e){Debug.log(this,"Could not close streams",e);}
	   }
    }
  }


}
