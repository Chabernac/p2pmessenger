/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

public class MessageException extends Exception {

  private static final long serialVersionUID = -1555888530021635410L;

  public MessageException () {
    super();
  }

  public MessageException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public MessageException ( String anMessage ) {
    super( anMessage );
  }

  public MessageException ( Throwable anCause ) {
    super( anCause );
  }
}
