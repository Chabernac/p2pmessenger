/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

public class StreamSplittingServerException extends Exception {

  private static final long serialVersionUID = -8875104085428387584L;

  public StreamSplittingServerException() {
    super();
  }

  public StreamSplittingServerException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public StreamSplittingServerException( String aMessage ) {
    super( aMessage );
  }

  public StreamSplittingServerException( Throwable aCause ) {
    super( aCause );
  }
}
