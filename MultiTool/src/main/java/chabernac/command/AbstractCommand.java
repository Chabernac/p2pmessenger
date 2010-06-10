package chabernac.command;

import java.util.Observable;

public abstract class AbstractCommand extends Observable implements ButtonCommand{
  
  public void notifyObs(){
    setChanged();
    notifyObservers();
  }
  
  public char getMnemonic(){
    return getName().charAt(0);
  }
}
