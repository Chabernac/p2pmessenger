package chabernac.utils;

import java.util.Observable;

public class DefaultObservable extends Observable {
  private Object myTarget = null;

  public DefaultObservable(Object aTarget) {
    super();
    myTarget = aTarget;
  }
  
  public void notifyObs(String argument){
    setChanged();
    notifyObservers(argument);
  }
  
  public Object getTarget(){
    return myTarget;
  }

}
