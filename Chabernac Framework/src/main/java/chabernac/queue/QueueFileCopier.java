package chabernac.queue;

import java.io.File;
import chabernac.utils.*;

public class QueueFileCopier extends QueueObserver
{
	private File destination = null;
	private File source = null;
	private File destinationFile = null;
	private boolean deleteSource = false;
	private boolean deletePreviousDestination = false;

	public QueueFileCopier (Queue fileQueue)
	{
		super(fileQueue);
	}

	public void processObject(Object o)
	{
		Debug.log(this,"Processing object: " + o.toString());
		if(o instanceof File)
		{
			if(deletePreviousDestination && destinationFile != null){destinationFile.delete();}
			source = (File)o;
			if(source!=null && source.exists())
			{
				Debug.log(this,"Copying file: " + source.toString() + " --> " + destination.toString());
				destinationFile = Explorer.copyFile(source,destination);
				if(deleteSource){source.delete();}
			}
		}
	}

	public void setDestination(File destination){this.destination = destination;}
	public File getDestination(){return destination;}
	public void setDeleteSource(boolean deleteSource){this.deleteSource= deleteSource;}
	public boolean isDeleteSource(){return deleteSource;}
	public void setDeletePreviousDestination(boolean deletePreviousDestination){this.deletePreviousDestination = deletePreviousDestination;}
	public boolean isDeletePreviousDestination(){return deletePreviousDestination;}

}


