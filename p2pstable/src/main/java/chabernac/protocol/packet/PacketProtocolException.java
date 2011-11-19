/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class PacketProtocolException extends Exception {

  private static final long serialVersionUID = 30236624615997800L;

  public PacketProtocolException() {
    super();
  }

  public PacketProtocolException( String aMessage, Throwable aCause ) {
    super( aMessage, aCause );
  }

  public PacketProtocolException( String aMessage ) {
    super( aMessage );
  }

  public PacketProtocolException( Throwable aCause ) {
    super( aCause );
  }

}
