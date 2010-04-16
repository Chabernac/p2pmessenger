package chabernac.queue;

import java.net.Socket;
import java.util.Vector;

public class SocketAnalyser extends QueueObserver
{
	private Queue outputQueue = null;

	public SocketAnalyser(Queue inputQueue, Queue outputQueue)
	{
		super(inputQueue);
		this.outputQueue = outputQueue;
	}

	public void processObject(Object o)
	{
		Vector theVector = (Vector)o;
		try
		{
			Socket socket = new Socket((String)theVector.elementAt(0),((Integer)theVector.elementAt(1)).intValue());
			outputQueue.put(socket);
		}catch(Exception e){}
	}
}

