/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.document;

public class DocumentationBaseException extends Exception {

  private static final long serialVersionUID = -7558054916028273811L;

  public DocumentationBaseException() {
    super();
  }

  public DocumentationBaseException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public DocumentationBaseException( String aMessage ) {
    super( aMessage );
  }

  public DocumentationBaseException( Throwable aCause ) {
    super( aCause );
    // TODO Auto-generated constructor stub
  }

}
