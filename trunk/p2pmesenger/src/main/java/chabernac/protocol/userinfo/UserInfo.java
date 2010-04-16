/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class UserInfo extends Observable{
  public static enum Status {OFFLINE, ONLINE, BUSY, AWAY};
  public static enum Property {ID, NAME, EMAIL, TELNR, LOCATION};
  
  private Map<Property, String> myProperties = new HashMap<Property, String>();
  private Status myStatus = Status.OFFLINE;
  
  public void setProperty(Property aProperty, String aValue){
    if(aValue == null) throw new IllegalArgumentException("Property value can not be null");
    Object theOldValue = myProperties.get( aProperty );
    myProperties.put( aProperty, aValue );
    if(!aValue.equals( theOldValue )){
      notifyObs( aProperty );
    }
  }
  
  public String getProperty(Property aProperty){
    return myProperties.get( aProperty );
  }
  

  public Status getStatus() {
    return myStatus;
  }
  
  public void setStatus( Status anStatus ) {
    Status theOldStatus = myStatus;
    myStatus = anStatus;
    if(myStatus != theOldStatus){
      notifyObs( null );
    }
  }
  
  private void notifyObs(Property aChangeProperty){
    setChanged();
    notifyObservers(aChangeProperty);
  }
  
  public String getId(){
    return getProperty( Property.ID );
  }
  
  public void setId(String anId){
    setProperty( Property.ID, anId );
  }
  
  public String getName(){
    return getProperty( Property.NAME );
  }
  
  public void setName(String aName){
    setProperty( Property.NAME, aName );
  }

  public String getLocation(){
    return getProperty( Property.LOCATION );
  }
  
  public void setLocation(String aLocation){
    setProperty( Property.LOCATION, aLocation );
  }
  
  public String getEMail(){
    return getProperty( Property.EMAIL );
  }
  
  public void setEMail(String anEMail){
    setProperty( Property.EMAIL, anEMail );
  }
  
  public String getTelNr(){
    return getProperty( Property.TELNR );
  }
  
  public void setTelNr(String aTelNr){
    setProperty( Property.TELNR, aTelNr );
  }
}
