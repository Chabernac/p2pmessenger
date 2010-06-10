/*
 * Created on 9-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.object;

import java.io.Serializable;

public class ObjectReference implements Serializable{
	private int myObjectId;
	
	public ObjectReference(int anId){
		myObjectId = anId;
	}
	
	public int getId(){
		return myObjectId;
	}

}
