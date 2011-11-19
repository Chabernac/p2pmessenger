/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

/*
 * Inmutable class repressenting a delivery report
 */
public class DeliveryReport {
  public static enum Status{DELIVERED, FAILED, IN_PROGRESS};
  
  private final Status myDeliveryStatus;
  private final Message myMessage;
  private final MultiPeerMessage myMultiPeerMessage;
  
  public DeliveryReport ( MultiPeerMessage aMultiPeerMessage, Status anDeliveryStatus , Message anMessage ) {
    super();
    myDeliveryStatus = anDeliveryStatus;
    myMultiPeerMessage = aMultiPeerMessage;
    myMessage = anMessage;
  }

  public Status getDeliveryStatus() {
    return myDeliveryStatus;
  }

  public Message getMessage() {
    return myMessage;
  }

  public MultiPeerMessage getMultiPeerMessage() {
    return myMultiPeerMessage;
  }
  
  public String toString(){
    return myDeliveryStatus.name() + " " + myMultiPeerMessage.toString();
  }
}
