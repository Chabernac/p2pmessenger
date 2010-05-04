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
  
  public DeliveryReport ( Status anDeliveryStatus , Message anMessage ) {
    super();
    myDeliveryStatus = anDeliveryStatus;
    myMessage = anMessage;
  }

  public Status getDeliveryStatus() {
    return myDeliveryStatus;
  }

  public Message getMessage() {
    return myMessage;
  }
}
