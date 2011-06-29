/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.Serializable;

public class FilePacket implements Serializable{
  private static final long serialVersionUID = -5514122169065728215L;
  private final String myId;
  private final byte[] myBytes;
  private final int myPacket;
  
  public FilePacket( String aId, byte[] aBytes, int aPacket ) {
    super();
    myId = aId;
    myBytes = aBytes;
    myPacket = aPacket;
  }

  public String getId() {
    return myId;
  }

  public byte[] getBytes() {
    return myBytes;
  }

  public int getPacket() {
    return myPacket;
  }
}
