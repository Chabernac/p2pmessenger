/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;


public abstract class Protocol implements IProtocol{
  protected IProtocol myParentProtocol = null;
  
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
   * prefix the message with the id of the protocol
   * 
   * @param aMessage
   * @return
   */
  public String createMessage(String aMessage){
    return myId + aMessage;
  }
  
  @Override
  public void setMasterProtocol( IProtocol aProtocol ) {
    myParentProtocol = aProtocol;
  }
  
  public IProtocol getMasterProtocol(){
    return myParentProtocol;
  }
  
  public ProtocolContainer findProtocolContainer(){
    if(myParentProtocol instanceof ProtocolContainer){
      return (ProtocolContainer)myParentProtocol;
    }
    if(myParentProtocol instanceof Protocol){
      return ((Protocol)myParentProtocol).findProtocolContainer();
    }
    return null;
  }
}
