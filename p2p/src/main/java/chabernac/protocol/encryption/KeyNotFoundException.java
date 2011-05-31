/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

public class KeyNotFoundException extends Exception {

  private static final long serialVersionUID = 5102825306986713111L;

  public KeyNotFoundException() {
    super();
  }

  public KeyNotFoundException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public KeyNotFoundException( String aMessage ) {
    super( aMessage );
  }

  public KeyNotFoundException( Throwable aCause ) {
    super( aCause );
  }

}
