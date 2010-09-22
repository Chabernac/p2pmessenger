/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

public class CometException extends Exception {

  private static final long serialVersionUID = 5123340337987023712L;

  public CometException () {
    super();
  }

  public CometException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public CometException ( String anMessage ) {
    super( anMessage );
  }

  public CometException ( Throwable anCause ) {
    super( anCause );
  }

}
