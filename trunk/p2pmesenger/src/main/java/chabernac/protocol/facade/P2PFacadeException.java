/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

public class P2PFacadeException extends Exception {

  private static final long serialVersionUID = -7322219455381224045L;

  public P2PFacadeException () {
    super();
  }

  public P2PFacadeException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public P2PFacadeException ( String anMessage ) {
    super( anMessage );
  }

  public P2PFacadeException ( Throwable anCause ) {
    super( anCause );
  }

}
