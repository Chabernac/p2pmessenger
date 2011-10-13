/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class AsyncTransferException extends Exception{
  private static final long serialVersionUID = -5905631830705759144L;

  public AsyncTransferException () {
    super();
  }

  public AsyncTransferException ( String aMessage , Throwable aCause ) {
    super( aMessage, aCause );
  }

  public AsyncTransferException ( String aMessage ) {
    super( aMessage );
  }

  public AsyncTransferException ( Throwable aCause ) {
    super( aCause );
  }
}
