/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.encryption;

import java.io.File;

import junit.framework.TestCase;

public class StoredPrivateKeyEncryptionTest extends TestCase {
  
  public void testEncryption() throws EncryptionException{
    StoredPrivateKeyEncryption theEncryption = new StoredPrivateKeyEncryption(new File("."), System.getProperty( "user.name" ));
    
    //let's store our own public key in the map, normally we will not do this, but now just for test purposes
    theEncryption.storePublicKeyForUser( System.getProperty( "user.name" ), theEncryption.getPublicKey() );  
    
    String theMessage = "The quick brown fox jumps easily over the fat and lazy dog";
    
    byte[] theEncryptedBytes = theEncryption.encryptMessageForUser( System.getProperty( "user.name" ), theMessage.getBytes() );
    
    String theDecodedString = new String(theEncryption.decryptMessage( theEncryptedBytes ));
    
    assertEquals( theMessage, theDecodedString );
  }
  
  public void testBiEncryption() throws EncryptionException{
    StoredPrivateKeyEncryption theEncryptorOfGuy = new StoredPrivateKeyEncryption(new File("."), "Guy");
    StoredPrivateKeyEncryption theEncryptorOfLeslie = new StoredPrivateKeyEncryption(new File("."), "Leslie");
    StoredPrivateKeyEncryption theEncryptorOfKoen = new StoredPrivateKeyEncryption(new File("."), "Koen");
    
    theEncryptorOfGuy.storePublicKeyForUser( "Leslie", theEncryptorOfLeslie.getPublicKey() );
    theEncryptorOfLeslie.storePublicKeyForUser( "Guy", theEncryptorOfGuy.getPublicKey() );
    
    String theMessage = "The quick brown fox jumps easily over the fat and lazy dog";
    
    //now send an encrypted message from guy to leslie
    byte[] theEnctryptedMessage = theEncryptorOfGuy.encryptMessageForUser( "Leslie", theMessage.getBytes() );
    
    //now let leslie decrypte the message
    assertEquals( theMessage, new String(theEncryptorOfLeslie.decryptMessage( theEnctryptedMessage )));
    
    //now let Koen also try to decrypt the message, this should fail with an exception
    try{
      theEncryptorOfKoen.decryptMessage( theEnctryptedMessage );
      fail("Koen should not be able to decrypt this message");
    }catch(Exception e){
    }
    
  }
}
