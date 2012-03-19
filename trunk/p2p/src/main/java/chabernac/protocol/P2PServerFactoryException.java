/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class P2PServerFactoryException extends Exception {
  private static final long serialVersionUID = -6917691970449768294L;

  public P2PServerFactoryException() {
    super();
  }

  public P2PServerFactoryException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public P2PServerFactoryException( String aMessage ) {
    super( aMessage );
  }

  public P2PServerFactoryException( Throwable aCause ) {
    super( aCause );
  }

}
