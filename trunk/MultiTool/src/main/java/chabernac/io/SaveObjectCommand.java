/*
 * Created on 12-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.io;

import java.io.File;

import chabernac.command.Command;

public class SaveObjectCommand implements Command{
	private Object myObject = null;
	private File myFile = null;
	
	public SaveObjectCommand(Object anObject, File aFile){
		myObject = anObject;
		myFile = aFile;
	}
	
	public void execute(){
		IOOperator.saveObject(myObject, myFile);
	}
}