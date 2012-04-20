/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chabernac.tools.PropertyMap;

public class SessionData {
  private Map<String, PropertyMap> mySessionData = Collections.synchronizedMap( new HashMap<String, PropertyMap>());
  
  public void putProperty(String aSession, String aKey, Object aValue){
    if(aSession == null || aKey == null || aValue == null) return;
    
    if(!mySessionData.containsKey( aSession )){
      mySessionData.put( aSession, new PropertyMap() );
    }
    
    mySessionData.get( aSession ).setProperty( aKey, aValue );
  }
  
  public Object removeProperty(String aSession, String aKey){
    if(aSession == null || aKey == null ) return null;
    PropertyMap theProperties = mySessionData.get(aSession);
    if(theProperties != null){
      return theProperties.remove( aKey );
    }
    return null;
  }
  
  public Object getProperty(String aSession, String aKey){
   if(!mySessionData.containsKey( aSession )) return null;
   return mySessionData.get( aSession ).get( aKey );
  }
  
  public PropertyMap clearSessionData(String aSession){
    return mySessionData.remove( aSession );
  }
  
  public boolean containsSession(String aSession){
    return mySessionData.containsKey( aSession );
  }
}
