/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

import java.io.File;
import java.util.Properties;

import chabernac.encryption.EncryptionException;
import chabernac.encryption.StoredPrivateKeyEncryption;
import chabernac.encryption.iPublicPrivateKeyEncryption;

public class PublicPrivateKeyEnctryptionFactory implements iPublicPrivateKeyEncryptionFactory {
  private Properties myProperties = null;
  
  public PublicPrivateKeyEnctryptionFactory(Properties aProperties){
    myProperties = aProperties;
  }

  @Override
  public iPublicPrivateKeyEncryption createEncryption() throws PublicPrivateKeyEnctryptionException{
    File theLocation = new File(myProperties.getProperty( "key.location", "." ));
    String theUser = myProperties.getProperty( "key.user", System.getProperty( "user.name" ) );
    try {
      return new StoredPrivateKeyEncryption( theLocation, theUser );
    } catch ( EncryptionException e ) {
      throw new PublicPrivateKeyEnctryptionException("Unable to instantiate StoredPrivateKeyEncryption");
    }
  }

}
