/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public class InvalidIpAddressException extends Exception {
  private static final long serialVersionUID = 7498900550237440971L;

  public InvalidIpAddressException() {
    super();
  }

  public InvalidIpAddressException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public InvalidIpAddressException( String aMessage ) {
    super( aMessage );
  }

  public InvalidIpAddressException( Throwable aCause ) {
    super( aCause );
  }

}
