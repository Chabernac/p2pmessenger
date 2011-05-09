/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

public class EndPointNotAvailableException extends CometException {

  private static final long serialVersionUID = 5537112619937594902L;

  public EndPointNotAvailableException() {
    super();
  }

  public EndPointNotAvailableException( String anMessage, Throwable anCause ) {
    super( anMessage, anCause );
  }

  public EndPointNotAvailableException( String anMessage ) {
    super( anMessage );
  }

  public EndPointNotAvailableException( Throwable anCause ) {
    super( anCause );
  }

}
