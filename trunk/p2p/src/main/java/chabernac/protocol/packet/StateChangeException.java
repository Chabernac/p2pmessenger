/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class StateChangeException extends Exception {
  private static final long serialVersionUID = 5990575182220079268L;

  public StateChangeException () {
    super();
  }

  public StateChangeException ( String aMessage , Throwable aCause ) {
    super( aMessage, aCause );
  }

  public StateChangeException ( String aMessage ) {
    super( aMessage );
  }

  public StateChangeException ( Throwable aCause ) {
    super( aCause );
  }
}
