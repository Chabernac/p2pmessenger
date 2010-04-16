/*
 * Created on 10-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.io.File;

import chabernac.utils.Debug;
import chabernac.utils.Explorer;

public class FileCopier implements iObjectProcessor {

	private File destination = null;
	private File source = null;
	private File destinationFile = null;
	private boolean deleteSource = false;
	private boolean deletePreviousDestination = false;


	public boolean processObject(Object o)
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
				return true;
			}
		}
		return false;
	}

	public void setDestination(File destination){this.destination = destination;}
	public File getDestination(){return destination;}
	public void setDeleteSource(boolean deleteSource){this.deleteSource= deleteSource;}
	public boolean isDeleteSource(){return deleteSource;}
	public void setDeletePreviousDestination(boolean deletePreviousDestination){this.deletePreviousDestination = deletePreviousDestination;}
	public boolean isDeletePreviousDestination(){return deletePreviousDestination;}

}
