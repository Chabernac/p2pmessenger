package chabernac.queue;

import java.io.*;
import java.net.*;
import chabernac.utils.Debug;

public class SocketListener implements Runnable
{
	private Socket theSocket = null;
	private Queue theQueue = null;
	private boolean stop = false;
	private ObjectInputStream objectIn = null;

	public SocketListener(Socket aSocket, Queue aQueue)
	{
		theSocket = aSocket;
		theQueue = aQueue;
		getStreams();
		if(objectIn!=null){new Thread(this).start();}
	}

	private void getStreams()
	{
		try
		{
			objectIn = new ObjectInputStream(theSocket.getInputStream());
		}catch(Exception e){Debug.log(this,"Could nog get inputStream of socket",e);}
	}

	public void run()
	{
		while(!stop)
		{

		}
	}

	public void stop(){stop = true;}

}

