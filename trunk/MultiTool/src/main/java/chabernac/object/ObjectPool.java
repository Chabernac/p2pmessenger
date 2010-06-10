/*
 * Created on 8-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.object;

import java.util.ArrayList;
import java.util.Iterator;

public class ObjectPool {
	private ArrayList myIterableList = null;
	private iObjectIdConvertor myConvertor = null;
	
	public ObjectPool(){
		this(new DefaultObjectIdConvertor());
	}
	
	public ObjectPool(iObjectIdConvertor anIdConvertor){
		myIterableList = new ArrayList();
		myConvertor = anIdConvertor;
	}
	
	public void addIterable(Iterable anIterable){
		myIterableList.add(anIterable);
	}
	
	public Object findObject(int anId){
		Iterable myCurrentIterable = null;
		Iterator theIterator = null;
		Object theObject = null;
		for(Iterator i=myIterableList.iterator();i.hasNext(); ){
			myCurrentIterable = (Iterable)i.next();
			theIterator = myCurrentIterable.iterator();
			while(theIterator.hasNext()){
				theObject = theIterator.next();
				if(myConvertor.getObjectId(theObject) == anId) return theObject;
			}
		}
		return null;
	}
	
	public iObjectIdConvertor getConvertor(){
		return myConvertor;
	}
	
	
	

}
