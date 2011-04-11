/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

public class ReactorException extends Exception {

  private static final long serialVersionUID = 5776187253685119166L;

  public ReactorException() {
    super();
  }

  public ReactorException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public ReactorException( String aMessage ) {
    super( aMessage );
  }

  public ReactorException( Throwable aCause ) {
    super( aCause );
  }

}
