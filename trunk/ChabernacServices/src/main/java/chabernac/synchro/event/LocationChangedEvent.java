package chabernac.synchro.event;

import chabernac.record.RS001;
import chabernac.synchro.SynchronizedEvent;
import chabernac.synchro.SynchronizedRecord;
import chabernac.utils.Record;

public class LocationChangedEvent extends SynchronizedEvent {
  
  private int myPlayer;
  private int myX;
  private int myY;
  
  public LocationChangedEvent(){
    
  }
  
  public LocationChangedEvent(int aPlayer, int aX, int aY){
    myPlayer = aPlayer;
    myX = aX;
    myY = aY;
  }

  public int getPlayer() {
    return myPlayer;
  }

  public void setPlayer(int anPlayer) {
    myPlayer = anPlayer;
  }

  public int getX() {
    return myX;
  }

  public void setX(int anX) {
    myX = anX;
  }

  public int getY() {
    return myY;
  }

  public void setY(int anY) {
    myY = anY;
  }
  
  public SynchronizedRecord getRecord() {
    RS001 theRecord = new RS001();
    theRecord.setValue("PLAYER", myPlayer);
    theRecord.setValue("X", myX);
    theRecord.setValue("Y", myY);
    return theRecord;
  }

  public void setRecord(Record aRecord) {
    myPlayer = aRecord.getIntValue("PLAYER");
    myX = aRecord.getIntValue("X");
    myY = aRecord.getIntValue("Y");
  }
  
  public String toString(){
    return "<player id='" + myPlayer + "' x='" + myX + "' y='" + myY + "'/>";
  }

}
