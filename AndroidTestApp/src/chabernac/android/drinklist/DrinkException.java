/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

public class DrinkException extends Exception {
  private static final long serialVersionUID = -6823539918152387878L;

  public DrinkException() {
    super();
  }

  public DrinkException( String aDetailMessage, Throwable aThrowable ) {
    super( aDetailMessage, aThrowable );
  }

  public DrinkException( String aDetailMessage ) {
    super( aDetailMessage );
  }

  public DrinkException( Throwable aThrowable ) {
    super( aThrowable );
  }

}
