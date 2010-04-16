package chabernac.queue;

import java.util.Hashtable;
import java.util.Vector;

import chabernac.log.Logger;

public class ObjectDispatcher implements iObjectProcessor {
  private Hashtable myProcessors = null;
  
  public ObjectDispatcher(){
    myProcessors = new Hashtable();
  }

  public void processObject(Object anObject) {
    Class theClass = anObject.getClass();
    if(myProcessors.containsKey(theClass)){
      Vector theProcessors = getProcessors(theClass);
      for(int i=0;i<theProcessors.size();i++){
        iObjectProcessor theProcessor = (iObjectProcessor)theProcessors.elementAt(i);
        theProcessor.processObject(anObject);
      }
    } else {
      Logger.log(this,"No processor found for object of class: " + theClass.toString());
    }
  }
  
  public void setProcessor(Class aClass, iObjectProcessor aProcessor){
    Vector theProcessors = getProcessors(aClass);
    theProcessors.add(aProcessor);
  }
  
  public void removeProcessor(Class aClass, iObjectProcessor aProcessor){
    Vector theProcessors = getProcessors(aClass);
    theProcessors.remove(aProcessor);
    if(theProcessors.isEmpty()){
      myProcessors.remove(aClass);
    }
  }
  
  private Vector getProcessors(Class aClass){
    if(!myProcessors.containsKey(aClass)){
      myProcessors.put(aClass, new Vector());
    }
    return (Vector)myProcessors.get(aClass);
  }

}
