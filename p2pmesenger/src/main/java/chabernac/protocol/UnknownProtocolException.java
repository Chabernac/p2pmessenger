/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class UnknownProtocolException extends Exception {

  private static final long serialVersionUID = 3838374847142220378L;

  public UnknownProtocolException () {
    super();
  }

  public UnknownProtocolException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public UnknownProtocolException ( String anMessage ) {
    super( anMessage );
  }

  public UnknownProtocolException ( Throwable anCause ) {
    super( anCause );
  }
}
