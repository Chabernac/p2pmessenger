package chabernac.server;

import java.net.*;
import chabernac.utils.Debug;
import java.io.*;

public class Client implements Runnable
{
	private String server = null;
	private int port;
	private Class clientProtocolClass = null;
	private Protocol myProtocol = null;
	private boolean stop = false;
	private boolean running = false;

	public Client(String server, int port, Protocol aProtocol)
	{
		this.port = port;
		this.server = server;
		this.myProtocol = aProtocol;
	}

	public Client(String server, int port, Class clientProtocolClass)
	{
		this.server = server;
		this.port = port;
		//this.clientProtocol = clientProtocol;
		this.clientProtocolClass = clientProtocolClass;
	}

	public synchronized void run()
	{
		Debug.log(this,"Starting client to server: " + server + " on port " + port + " ...");
		Socket clientSocket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
	  	Protocol clientProtocol = null;
		try
		{
			Debug.log(this,"Starting clientprotocol...");
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
		}catch(Exception e){Debug.log(this,"Could net set up client",e);}
		 finally
		 {
			try
			   {
			   if(inputStream != null) { inputStream.close(); }
			   if(outputStream != null) {
				   outputStream.flush();
			   	   outputStream.close();
			   }
			   if(clientSocket != null) { clientSocket.close(); }
			   }catch(Exception e){Debug.log(this,"Could not close streams",e);}
			 running = false;
			 notify();
		 }

	}

	private synchronized void started()
	{
		Debug.log(this,"Notifying...");
		notifyAll();
	}



	public synchronized boolean waitTillStarted()
	{
		try
		{
			wait();
		}catch(Exception e){Debug.log(this,"Waiting interrupted",e);}
		Debug.log(this,"returning: " + running);
		return running;
	}

	public boolean isRunning(){return running;}

	public void start()
	{
		new Thread(this).start();
	}
}
