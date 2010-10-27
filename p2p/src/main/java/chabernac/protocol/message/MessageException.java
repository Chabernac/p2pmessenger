/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import chabernac.protocol.message.MessageProtocol.Response;

public class MessageException extends Exception {

  private static final long serialVersionUID = -1555888530021635410L;
  private Response myResponse = null;

  public MessageException () {
    super();
  }

  public MessageException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public MessageException ( String anMessage ) {
    super( anMessage );
  }
  
  public MessageException ( String anMessage, Response aResponseCode ) {
    super( anMessage );
    myResponse = aResponseCode;
  }

  public MessageException ( Throwable anCause ) {
    super( anCause );
  }

  public Response getResponse() {
    return myResponse;
  }
  
  
}
