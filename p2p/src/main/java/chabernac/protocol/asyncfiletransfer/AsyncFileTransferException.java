/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

public class AsyncFileTransferException extends Exception {

  private static final long serialVersionUID = -3154972907110351938L;

  public AsyncFileTransferException() {
    super();
  }

  public AsyncFileTransferException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public AsyncFileTransferException( String aMessage ) {
    super( aMessage );
  }

  public AsyncFileTransferException( Throwable aCause ) {
    super( aCause );
  }

}
