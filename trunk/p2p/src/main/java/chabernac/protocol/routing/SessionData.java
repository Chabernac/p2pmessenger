/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SessionData {
  private Map<String, Properties> mySessionData = Collections.synchronizedMap( new HashMap<String, Properties>());
  
  public void putProperty(String aSession, String aKey, String aValue){
    if(!mySessionData.containsKey( aSession )){
      mySessionData.put( aSession, new Properties() );
    }
    
    mySessionData.get( aSession ).setProperty( aKey, aValue );
  }
  
  public String getProperty(String aSession, String aKey){
   if(!mySessionData.containsKey( aSession )) return null;
   return mySessionData.get( aSession ).getProperty( aKey );
  }
  
  public Properties clearSessionData(String aSession){
    return mySessionData.remove( aSession );
  }
  
  public boolean containsSession(String aSession){
    return mySessionData.containsKey( aSession );
  }
}
