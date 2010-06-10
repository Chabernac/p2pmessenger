package chabernac.util;

import chabernac.event.Event;

public class StatusEvent extends Event{
  public static final int MESSAGE = 1;
  public static final int WARNING = 2;
  public static final int ERROR = 3;
  
  private int myType;
  
  public StatusEvent(int aType, String aDescription){
    super(aDescription);
    myType = aType;
  }

  public int getType() {
    return myType;
  }

  public void setType(int anType) {
    myType = anType;
  }

}
