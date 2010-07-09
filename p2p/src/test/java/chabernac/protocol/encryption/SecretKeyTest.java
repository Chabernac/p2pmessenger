/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

public class SecretKeyTest extends TestCase {
  public void testSecretKey() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
    keyGen.init(new SecureRandom());
    SecretKey theSecretKey = keyGen.generateKey();
    assertNotNull( theSecretKey.getEncoded() );
    assertTrue( theSecretKey.getEncoded().length > 0 );
    
    SecretKey theSecretKey2 = new SecretKeySpec(theSecretKey.getEncoded(), "DES");
    
    assertEquals( theSecretKey.getEncoded(), theSecretKey2.getEncoded() );

    
    Cipher theCipher = Cipher.getInstance ( theSecretKey.getAlgorithm () ) ;
    theCipher.init ( Cipher.ENCRYPT_MODE, theSecretKey) ;
    byte[] theEncrypted = theCipher.doFinal( "the quick brown fox jumps easily over the quick and lazy dog".getBytes() );
    
    Cipher theCipher2 = Cipher.getInstance ( theSecretKey2.getAlgorithm () ) ;
    theCipher2.init ( Cipher.DECRYPT_MODE, theSecretKey2) ;
    byte[] theDecrypted = theCipher2.doFinal( theEncrypted );
    
    assertEquals( "the quick brown fox jumps easily over the quick and lazy dog", new String(theDecrypted) );
    
    
  }
  
  public void assertEquals(byte[] aBytes, byte[] aBytes2){
    assertEquals( aBytes.length, aBytes2.length );
    for(int i=0;i<aBytes.length;i++){
      assertEquals( aBytes[i], aBytes2[i]);
    }
  }
}
