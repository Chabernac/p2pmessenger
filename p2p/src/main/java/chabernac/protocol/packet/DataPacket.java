/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class DataPacket {
  private final String myId;
  private final byte[] myData;
  
  public DataPacket ( String aId , byte[] aData ) {
    super();
    myId = aId;
    myData = aData;
  }

  public String getId() {
    return myId;
  }

  public byte[] getBytes() {
    return myData;
  }
}
