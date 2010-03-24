/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class ProtocolException extends Exception {

  private static final long serialVersionUID = 3838374847142220378L;

  public ProtocolException () {
    super();
  }

  public ProtocolException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public ProtocolException ( String anMessage ) {
    super( anMessage );
  }

  public ProtocolException ( Throwable anCause ) {
    super( anCause );
  }
}
