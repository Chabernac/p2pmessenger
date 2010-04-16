package chabernac.data;

import java.util.*;

public class HashPreferences extends HashMap{
	public HashPreferences(){
		super();
	}

	public Object get(String key, Object aDefault){
		Object theObject = get(key);
		if(theObject == null){
			return aDefault;
		}
		return theObject;
	}

	public void putInt(String key, int aInt){
		put(key, new Integer(aInt));
	}

	public int getInt(String key, int aDef){
		Object aObject = get(key);
		if(aObject != null && aObject instanceof Integer){
			return ((Integer)aObject).intValue();
		} else {
			return aDef;
		}
	}

	public void putFloat(String key, float aFloat){
		put(key, new Float(aFloat));
	}

	public float getFloat(String key, float aDef){
		Object aObject = get(key);
		if(aObject != null && aObject instanceof Float){
			return ((Float)aObject).floatValue();
		} else {
			return aDef;
		}
	}

	public void putBoolean(String key, boolean aBoolean){
		put(key, new Boolean(aBoolean));
	}

	public boolean getBoolean(String key, boolean aDef){
		Object aObject = get(key);
		if(aObject != null && aObject instanceof Boolean){
			return ((Boolean)aObject).booleanValue();
		} else {
			return aDef;
		}
	}


}