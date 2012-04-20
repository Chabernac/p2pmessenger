/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.version;


public class VersionProtocolException extends Exception {

  private static final long serialVersionUID = -1283065909013167419L;

  public VersionProtocolException () {
    super();
  }

  public VersionProtocolException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public VersionProtocolException ( String anMessage ) {
    super( anMessage );
  }

  public VersionProtocolException ( Throwable anCause ) {
    super( anCause );
  }

}
