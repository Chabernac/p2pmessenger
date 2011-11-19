/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.io.File;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;

import junit.framework.TestCase;

public class PublicKeyStoreTest extends TestCase {
  
  protected void tearDown(){
    File theStore = new File("store.bin");
    if(theStore.exists()){
      theStore.delete();
    }
  }
  
  

  public void testStore() throws EncryptionException, KeyNotFoundException{
    PublicKeyStore theStore = new PublicKeyStore( new File("store.bin"), false );

    PublicKey thePKey1 = generatePublicKey();
    PublicKey thePKey2 = generatePublicKey();

    theStore.storeKey( "1", thePKey1 );
    theStore.storeKey( "2", thePKey2 );

    assertTrue( theStore.containsKeyFor( "1" ) );
    assertTrue( theStore.containsKeyFor( "2" ) );
    assertFalse( theStore.containsKeyFor( "3" ) );

    assertEquals( thePKey1, theStore.getKey( "1" ) );
    assertEquals( thePKey2, theStore.getKey( "2" ) );
  }

  public void testStoreLoad() throws EncryptionException{
    PublicKeyStore theStore = new PublicKeyStore( new File("store.bin"), false );

    PublicKey thePKey1 = generatePublicKey();
    PublicKey thePKey2 = generatePublicKey();

    theStore.storeKey( "1", thePKey1 );
    theStore.storeKey( "2", thePKey2 );

    theStore.store();

    PublicKeyStore theStore2 = new PublicKeyStore( new File("store.bin"), false );
    theStore2.load();

    assertTrue( theStore.containsKeyFor( "1" ) );
    assertTrue( theStore.containsKeyFor( "2" ) );
    assertFalse( theStore.containsKeyFor( "3" ) );
  }
  
  public void testStoreOnUpdate() throws EncryptionException{
    PublicKeyStore theStore = new PublicKeyStore( new File("store.bin"), true );

    PublicKey thePKey1 = generatePublicKey();
    PublicKey thePKey2 = generatePublicKey();

    theStore.storeKey( "1", thePKey1 );
    theStore.storeKey( "2", thePKey2 );

    PublicKeyStore theStore2 = new PublicKeyStore( new File("store.bin"), false );
    theStore2.load();

    assertTrue( theStore.containsKeyFor( "1" ) );
    assertTrue( theStore.containsKeyFor( "2" ) );
    assertFalse( theStore.containsKeyFor( "3" ) );
  }

  public void testExceptionWhenKeyNotFound(){
    PublicKeyStore theStore = new PublicKeyStore( new File("store.bin"), false );

    try{
      theStore.getKey( "1" );
      fail("Should not get here");
    }catch(Exception e){
    }
  }

  public PublicKey generatePublicKey() throws EncryptionException{
    try{
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      keyGen.initialize(1024, random);
      return keyGen.generateKeyPair().getPublic();
    }catch(Exception e){
      throw new EncryptionException("Could not generate key pair", e);
    }
  }

}
