package chabernac.application;

import java.util.Hashtable;

public class RefBase extends Hashtable {
  public static RefBase myRefBase = null;
  
  public static RefBase getInstance(){
    if(myRefBase == null) myRefBase = new RefBase();
    return myRefBase;
  }
  
  public static void putObject(Object aKey, Object aValue){
    RefBase theRefBase = getInstance();
    theRefBase.put(aKey, aValue);
  }
  
  public static Object getObject(Object aKey){
    RefBase theRefBase = getInstance();
    return theRefBase.get(aKey);
  }
  
  public static boolean containsKeyObject(Object aKey){
    RefBase theRefBase = getInstance();
    return theRefBase.containsKey(aKey);
  }
}
