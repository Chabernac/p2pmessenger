/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.xmlparser;

public class XMLWordParserException extends Exception {

  private static final long serialVersionUID = 1785690979001097603L;

  public XMLWordParserException() {
    super();
  }

  public XMLWordParserException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public XMLWordParserException( String aMessage ) {
    super( aMessage );
  }

  public XMLWordParserException( Throwable aCause ) {
    super( aCause );
  }

}
