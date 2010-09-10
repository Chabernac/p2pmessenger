/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

public class DataHandlingException extends Exception {
  public DataHandlingException () {
    super();
  }

  public DataHandlingException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public DataHandlingException ( String anMessage ) {
    super( anMessage );
  }

  public DataHandlingException ( Throwable anCause ) {
    super( anCause );
  }

}
