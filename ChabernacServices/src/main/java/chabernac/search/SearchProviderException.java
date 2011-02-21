/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.search;

public class SearchProviderException extends Exception {

  private static final long serialVersionUID = 353181413566694933L;

  public SearchProviderException() {
    super();
  }

  public SearchProviderException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public SearchProviderException( String aMessage ) {
    super( aMessage );
  }

  public SearchProviderException( Throwable aCause ) {
    super( aCause );
  }

}
