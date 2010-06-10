/*
 * Created on 9-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.object;

import java.util.ArrayList;
import java.util.List;

public class ListConvertor {
	private ObjectPool myPool = null;
	
	public ListConvertor(ObjectPool aPool){
		myPool = aPool;
	}
	
	public ArrayList convertList2Ids(List aCollection){
		ArrayList theList = new ArrayList();
		for(int i=0;i<aCollection.size();i++){
			theList.add(i, new ObjectReference(myPool.getConvertor().getObjectId(aCollection.get(i))));
		}
		return theList;
	}
	
	public List convertList2Objects(List aCollection){
		Object theObject = null;
		for(int i=0;i<aCollection.size();i++){
			theObject = aCollection.get(i);
			if(theObject instanceof ObjectReference){
				Object theReferedObject = myPool.findObject( ((ObjectReference)theObject).getId() );
				if(theReferedObject != null){
					aCollection.set(i, theReferedObject);
				} else {
				  aCollection.remove(i);
          i--;
        }
			}
		}
		return aCollection;
	}
	

}
