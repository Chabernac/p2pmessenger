/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

public class CamProtocolException extends Exception {
  private static final long serialVersionUID = -3366827286917753339L;

  public CamProtocolException () {
    super();
  }

  public CamProtocolException ( String message , Throwable cause ) {
    super( message, cause );
  }

  public CamProtocolException ( String message ) {
    super( message );
  }

  public CamProtocolException ( Throwable cause ) {
    super( cause );
  }
  
}
