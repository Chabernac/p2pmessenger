/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class Packet {
  private final String myFrom;
  private final String myTo;
  private final String myId;
  private final byte[] myBytes;
  
  public Packet( String aFrom, String aTo, String aId, byte[] aBytes ) {
    super();
    myFrom = aFrom;
    myTo = aTo;
    myId = aId;
    myBytes = aBytes;
  }

  public String getFrom() {
    return myFrom;
  }

  public String getTo() {
    return myTo;
  }

  public String getId() {
    return myId;
  }

  public byte[] getBytes() {
    return myBytes;
  }
}
