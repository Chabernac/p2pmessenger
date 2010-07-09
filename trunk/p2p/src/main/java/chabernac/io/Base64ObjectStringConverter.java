/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

public class Base64ObjectStringConverter <T extends Serializable >implements iObjectStringConverter< T > {

  @Override
  public T getObject( String aString ) throws IOException {
    try{
      byte[] theBytes = Base64.decodeBase64( aString.getBytes() );
      ByteArrayInputStream theInputStream  = new ByteArrayInputStream(theBytes);
      ObjectInputStream theObjectInputStream = new ObjectInputStream(theInputStream);
      return (T)theObjectInputStream.readObject();
    }catch(Exception e){
      throw new IOException("Unable to read object from string", e);
    }

  }

  @Override
  public String toString( T anObject ) throws IOException {
    ByteArrayOutputStream theArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream theObjectOutputStream = new ObjectOutputStream(theArrayOutputStream);
    theObjectOutputStream.writeObject( anObject );
    byte[] theBytes = theArrayOutputStream.toByteArray();
    return new String(Base64.encodeBase64( theBytes, false ));
  }
  
}
