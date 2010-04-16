package chabernac.queue;

import java.io.File;

public class QueueFileDeleter extends QueueObserver
{
	public QueueFileDeleter(Queue fileQueue)
	{
		super(fileQueue);
	}

	public void processObject(Object o)
	{
		if(o instanceof File)
		{
			((File)o).delete();
		}
	}
}


