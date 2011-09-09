/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import chabernac.io.iObjectStringConverter;

public class PacketStringConverter implements iObjectStringConverter<Packet> {

  @Override
  public Packet getObject( String aString ) {
    String[] theParts= aString.split( ";" );
    return new Packet( theParts[0], theParts[1], theParts[2], theParts[3], Integer.parseInt(theParts[4]), Boolean.parseBoolean( theParts[5] ));
  }

  @Override
  public String toString( Packet aPacket ) {
    StringBuilder theBuilder = new StringBuilder();
    theBuilder.append( aPacket.getFrom() );
    theBuilder.append(";");
    theBuilder.append(aPacket.getTo());
    theBuilder.append(";");
    theBuilder.append(aPacket.getId());
    theBuilder.append(";");
    theBuilder.append(aPacket.getBytesAsString());
    theBuilder.append(";");
    theBuilder.append(aPacket.getHopDistance());
    theBuilder.append(";");
    theBuilder.append(aPacket.isSendResponse());
    return theBuilder.toString();
  }

}
