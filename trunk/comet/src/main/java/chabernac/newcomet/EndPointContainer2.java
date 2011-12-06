/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.newcomet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class EndPointContainer2 {
  private Map<String, EndPoint2> myEndPoints = new HashMap< String, EndPoint2 >();
  
  public synchronized EndPoint2 getEndPoint(String anEndPoint){
    if(!myEndPoints.containsKey( anEndPoint )){
      myEndPoints.put( anEndPoint, new EndPoint2( anEndPoint ) );
    }
    return myEndPoints.get(anEndPoint);
  }
  
  public Collection<EndPoint2> getEndPoints(){
    return myEndPoints.values();
  }
  
  public boolean containsEndPointFor(String anEndPoint){
    return myEndPoints.containsKey( anEndPoint );
  }
  
  public int size(){
    return myEndPoints.size();
  }
}
