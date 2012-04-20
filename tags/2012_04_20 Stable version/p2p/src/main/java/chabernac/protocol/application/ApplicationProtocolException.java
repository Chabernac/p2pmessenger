/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.application;

public class ApplicationProtocolException extends Exception {

  private static final long serialVersionUID = 7965317091286243326L;

  public ApplicationProtocolException () {
    super();
  }

  public ApplicationProtocolException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public ApplicationProtocolException ( String anMessage ) {
    super( anMessage );
  }

  public ApplicationProtocolException ( Throwable anCause ) {
    super( anCause );
  }

}
