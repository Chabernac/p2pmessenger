/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Protocol {
  protected Map<String, Protocol> mySubProtocols = null;
  
  protected String myId;
  
  public Protocol(String anId){
    if(anId.length() != 3) throw new IllegalArgumentException("Protocol identifier must be 3 bytes long");
    myId = anId;
  }
  
  public String getId(){
    return myId;
  }
  
  public abstract String getDescription();
  
  /**
   * this method must handle a single input command.
   * 
   * the session id is unique for each client which is connected.
   * 
   * the session id can be used to store user specific state data.
   * 
   * @param aSessionId
   * @param anInput
   * @return
   */
  protected abstract String handleCommand(long aSessionId, String anInput);
  
  public String handle(long aSessionId, String  anInput){
    Protocol theProtocol = findMatchingProtocol(anInput);
    if(theProtocol != null){
      String theInput = anInput.substring( 3 );
      return theProtocol.handle( aSessionId,  theInput);
    } else {
      return handleCommand( aSessionId, anInput );
    }
  }
  
  private Protocol findMatchingProtocol(String anInput){
    String theIdString = anInput.substring( 0, 3 );
    
    if(myId.equals( theIdString )) return this;
    
    if(mySubProtocols == null) return null;
    
    if(mySubProtocols.containsKey( theIdString )){
      return mySubProtocols.get(theIdString);
    }
    
    return null;
  }
  
  
  public void addSubProtocol(Protocol aSubProtocol){
    if(aSubProtocol.getId() == null){
      throw new IllegalArgumentException("Can only add a sub protocol which has an id");
    }
    
    if(mySubProtocols == null){
      mySubProtocols = Collections.synchronizedMap( new HashMap< String, Protocol > ());
    }
    
    if(mySubProtocols.containsKey( aSubProtocol.getId() )){
      throw new IllegalArgumentException("a subprotocol with this id is already registered");
    }
    
    mySubProtocols.put(aSubProtocol.getId(), aSubProtocol);
  }
  
  /**
   * prefix the message with the id of the protocol
   * 
   * @param aMessage
   * @return
   */
  public String createMessage(String aMessage){
    return myId + aMessage;
  }
}
