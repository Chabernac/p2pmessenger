/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public class NoAvailableNetworkAdapterException extends Exception {

  private static final long serialVersionUID = -8879138648463219392L;

  public NoAvailableNetworkAdapterException () {
    super();
  }

  public NoAvailableNetworkAdapterException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public NoAvailableNetworkAdapterException ( String anMessage ) {
    super( anMessage );
  }

  public NoAvailableNetworkAdapterException ( Throwable anCause ) {
    super( anCause );
  }

}
