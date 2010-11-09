/*
 * Created on 24-dec-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.control;

import java.util.Observable;
import java.util.Vector;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KeyMapContainer extends Observable{
	private Vector myKeyMapContainer;
	
	public KeyMapContainer(){
		myKeyMapContainer = new Vector();		
	}
	
	public void addKeyMap(KeyMap aKeyMap){
		myKeyMapContainer.addElement(aKeyMap);
	}
	
	public void removeKeyMap(KeyMap aKeyMap){
		myKeyMapContainer.remove(aKeyMap);
	}
	
	public Vector getKeyMap(){
		return myKeyMapContainer;
	}
	
	public int size(){
		return myKeyMapContainer.size();
	}
	
	public KeyMap keyMapAt(int i){
		return (KeyMap)myKeyMapContainer.elementAt(i);
	}
	
	public void notifyAllObs(){
		setChanged();
		notifyObservers();
	}

}
