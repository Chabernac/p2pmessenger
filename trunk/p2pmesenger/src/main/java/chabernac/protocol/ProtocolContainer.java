/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProtocolContainer implements IProtocol {
  public static enum Command {PROTOCOLS};
  public static enum Response {UNKNOWN_COMMAND};
  
  private Map<String, IProtocol> myProtocolMap = null;
  
  public ProtocolContainer(){
    addProtocol( this );
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    String theID = anInput.substring( 0, 3 );
    IProtocol theProtocol = myProtocolMap.get( theID );
    if(theProtocol == null){
      if(anInput.equalsIgnoreCase( Command.PROTOCOLS.name() )){
        return getProtocolString();
      }
      return Response.UNKNOWN_COMMAND.name();
    } else {
      return theProtocol.handleCommand( aSessionId, anInput.substring( 3 ) );
    }
    
  }
  
  public String getProtocolString() {
    StringBuilder theBuilder = new StringBuilder();
    for(String theId : myProtocolMap.keySet()){
      theBuilder.append( theId );
      theBuilder.append( ";" );
    }
    return theBuilder.toString();
  }

  public void addProtocol(IProtocol aProtocol) {
    if(aProtocol.getId() == null){
      throw new IllegalArgumentException("Can only add a sub protocol which has an id");
    }
    
    if(myProtocolMap == null){
      myProtocolMap = Collections.synchronizedMap( new HashMap< String, IProtocol > ());
    }
    
    if(myProtocolMap.containsKey( aProtocol.getId() )){
      throw new IllegalArgumentException("a subprotocol with this id is already registered");
    }
    
    myProtocolMap.put( aProtocol.getId(), aProtocol);
    aProtocol.setMasterProtocol( this );
  }

  @Override
  public String getId() {
    return "MAS";
  }

  @Override
  public void setMasterProtocol( IProtocol aProtocol ) {
  }

  @Override
  public void stop() {
    for(IProtocol theProtocol : myProtocolMap.values()){
      if(theProtocol != this){
        theProtocol.stop();
      }
    }
  }

}
