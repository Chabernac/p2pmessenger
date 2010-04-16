package chabernac.synchro.event;

import chabernac.record.RS002;
import chabernac.synchro.SynchronizedEvent;
import chabernac.synchro.SynchronizedRecord;
import chabernac.utils.Record;

public class FireEvent extends SynchronizedEvent {
  private int myPlayer;
  private int myX;
  private int myY;
  private boolean isFiring;

  public FireEvent(int aPlayer, int anX, int anY, boolean isFiring){
    myPlayer = aPlayer;
    myX = anX;
    myY = anY;
    this.isFiring = isFiring;
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
  
  public boolean isFiring() {
    return isFiring;
  }

  public void setFiring(boolean anIsFiring) {
    isFiring = anIsFiring;
  }

  public SynchronizedRecord getRecord() {
    RS002 theRecord = new RS002();
    theRecord.setValue("PLAYER", myPlayer);
    theRecord.setValue("X", myX);
    theRecord.setValue("Y", myY);
    theRecord.setValue("FIRING", isFiring);
    return theRecord;
  }

  public void setRecord(Record aRecord) {
    myPlayer = aRecord.getIntValue("PLAYER");
    myX = aRecord.getIntValue("X");
    myY = aRecord.getIntValue("Y");
  }

}
