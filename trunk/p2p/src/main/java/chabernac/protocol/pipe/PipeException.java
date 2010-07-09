/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

public class PipeException extends Exception {

  private static final long serialVersionUID = -7797013225161857869L;

  public PipeException () {
    super();
  }

  public PipeException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public PipeException ( String anMessage ) {
    super( anMessage );
  }

  public PipeException ( Throwable anCause ) {
    super( anCause );
  }

}
