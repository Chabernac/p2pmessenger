/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

public class InfoObject extends Observable implements Serializable, Map<String, Object>{
  private static final long serialVersionUID = 4813494112477269907L;
  private final Map<String, Object> myDelegate = new HashMap< String, Object >();

  public void clear() {
    myDelegate.clear();
  }

  public boolean containsKey( Object anKey ) {
    return myDelegate.containsKey( anKey );
  }

  public boolean containsValue( Object anValue ) {
    return myDelegate.containsValue( anValue );
  }

  public Set<Map.Entry< String, Object >> entrySet() {
    return myDelegate.entrySet();
  }

  public boolean equals( Object anO ) {
    return myDelegate.equals( anO );
  }

  public Object get( Object anKey ) {
    return myDelegate.get( anKey );
  }

  public int hashCode() {
    return myDelegate.hashCode();
  }

  public boolean isEmpty() {
    return myDelegate.isEmpty();
  }

  public Set<String> keySet() {
    return myDelegate.keySet();
  }

  public Object put( String anKey, Object anValue ) {
    Object theResult = myDelegate.put( anKey, anValue );
    setChanged();
    notifyObservers();
    return theResult;
  }

  public Object remove( Object anKey ) {
    return myDelegate.remove( anKey );
  }

  public int size() {
    return myDelegate.size();
  }

  public Collection<Object> values() {
    return myDelegate.values();
  }

  @Override
  public void putAll( Map< ? extends String, ? extends Object > anM ) {
    myDelegate.putAll( anM );
  }
  
  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    
    theBuilder.append( "InfoObject\r\n" );
    for(String theKey : myDelegate.keySet()){
      Object theObject = myDelegate.get(theKey);
      theBuilder.append(theKey);
      theBuilder.append("=");
      theBuilder.append(theObject.toString());
      theBuilder.append("\r\n");
    }
    return theBuilder.toString();
      
    
  }
  
}
