/*
 * Created on 10-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.io.File;

public class FileFilter implements iObjectFilter {
	
	private long size = 15000;
	
	public FileFilter(long aSize){
		size = aSize;
	}

	public boolean filter(Object anObject) {
		if(!(anObject instanceof File)) return false;
		File theFile = (File)anObject;
		if(theFile.length() < size) return false;
		return true;
		
	}
	
	public void setSize(long aSize){ size = aSize; }
	public long getSize(){ return size; }

}
