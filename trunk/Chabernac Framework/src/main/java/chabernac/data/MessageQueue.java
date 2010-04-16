package chabernac.data;

import java.util.Observer;
import java.util.Observable;
import java.util.Vector;
import chabernac.utils.*;

public class MessageQueue extends Vector{
  private MyObservable myObservable = null;
  private static final int myDefaultSize = 128;

  public MessageQueue(){
    this(myDefaultSize);
  }

  public MessageQueue(int aSize){
    super(aSize, 0);
    myObservable = new MyObservable();
  }

  public void addObserver(Observer anObserver){
    myObservable.addObserver(anObserver);
  }

  public void addMessage(String aMessage){
    if(size() >= capacity()) remove(0);
    Debug.log(this,"Adding message: " + aMessage);
    addElement(aMessage);
    myObservable.notifyAllObs();
  }

  public void clear(){
    super.clear();
    myObservable.notifyAllObs();
  }

  public String getLastMessage(){ return (String)lastElement(); }
  public String getAllMessages(){
    StringBuffer theBuffer = new StringBuffer();
    for(int i=0;i<size();i++){
      theBuffer.append((String)elementAt(i));
      theBuffer.append("\n");
    }
    return theBuffer.toString();
  }

  private class MyObservable extends Observable{
    public void notifyAllObs(){
      setChanged();
      notifyObservers();
    }
  }

}