/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

public class StoredPrivateKeyEncryption implements iPublicPrivateKeyEncryption {
  private File myKeyLocationDirectory = null;
  private KeyPair myKeyPair = null;
  private SecretKey mySecretKey = null;
  private String myUser = null;
  
  private Map<String, PublicKey > myPublicKeyStore = Collections.synchronizedMap( new HashMap<String, PublicKey >());
  private Map<String, SecretKey > mySecretKeyStore = Collections.synchronizedMap( new HashMap<String, SecretKey >());
  
  public StoredPrivateKeyEncryption(File aKeyLocationDirectory, String aUser) throws EncryptionException{
    myKeyLocationDirectory = aKeyLocationDirectory;
    myUser = aUser;
    loadPrivateKey();
  }

  private void loadPrivateKey() throws EncryptionException{
    File theFile = getKeyLocation();
    if(!theFile.exists()){
      //lets's generate the private key
      generateKey();
    } else {
      //load the private key
      loadKey();
    }
  }
  
  private File getKeyLocation() throws EncryptionException{
    if(!myKeyLocationDirectory.isDirectory()) throw new EncryptionException("Given path is not a directory");
    return new File(myKeyLocationDirectory.getAbsolutePath() + "/" + myUser + "_key.bin");
  }

  private void loadKey() throws EncryptionException {
    ObjectInputStream theInputStream = null;
    try{
      theInputStream = new ObjectInputStream(new FileInputStream(getKeyLocation()));
      myKeyPair = (KeyPair)theInputStream.readObject();
    } catch ( Exception e ) {
      throw new EncryptionException("Could not load key", e);
    }finally{
      if(theInputStream != null){
        try {
          theInputStream.close();
        } catch ( IOException e ) {
        }
      }
    }
  }

  public void generateKey() throws EncryptionException{
    ObjectOutputStream theOutputStream = null;
    try{
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
  
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      keyGen.initialize(1024, random);
      myKeyPair = keyGen.generateKeyPair();
      theOutputStream = new ObjectOutputStream(new FileOutputStream(getKeyLocation()));
      theOutputStream.writeObject(  myKeyPair );
      theOutputStream.flush();
      theOutputStream.close();
    }catch(Exception e){
      throw new EncryptionException("Could not generate key", e);
    } finally {
      if(theOutputStream != null){
        try {
          theOutputStream.close();
        } catch ( IOException e ) {
        }
      }
    }
  }

  @Override
  public byte[] decryptMessage( byte[] aMessage ) throws EncryptionException {
    if(myKeyPair == null) throw new EncryptionException("No local key pair present");
    
    try{
      Cipher theCipher = Cipher.getInstance ( myKeyPair.getPrivate().getAlgorithm () ) ;
      theCipher.init ( Cipher.DECRYPT_MODE, myKeyPair.getPrivate()) ;
      return theCipher.doFinal( aMessage );
    }catch(Exception e){
      throw new EncryptionException("Could not decrypt message", e);
    }
  }

  @Override
  public byte[] encryptMessageForUser( String aUser, byte[] aMessage ) throws EncryptionException {
    if(!myPublicKeyStore.containsKey( aUser )){
      throw new EncryptionException("Public key of user '" + aUser + "' not present in key store");
    }
    
    PublicKey theKey = myPublicKeyStore.get( aUser );
    
    try{
      Cipher theCipher = Cipher.getInstance ( theKey.getAlgorithm () ) ;
      theCipher.init ( Cipher.ENCRYPT_MODE, theKey) ;
      return theCipher.doFinal(  aMessage );
    }catch(Exception e){
      throw new EncryptionException("Could not encrypt message for user '" + aUser + "'", e);
    }
    
  }

  @Override
  public PublicKey getPublicKeyForUser( String aUser ) {
    return myPublicKeyStore.get(aUser);
  }

  @Override
  public void storePublicKeyForUser( String aUser, PublicKey aKey ) {
    myPublicKeyStore.put(aUser, aKey);
  }

  @Override
  public PublicKey getPublicKey() throws EncryptionException{
    if(myKeyPair == null ) throw new EncryptionException("No key present");
    return myKeyPair.getPublic();
  }

  @Override
  public String getUser() {
    return myUser;
  }

  @Override
  public SecretKey getSecretKeyForUser( String aUser ) {
    return mySecretKeyStore.get( aUser );
  }

  @Override
  public void storeSecretKeyForUser( String aUser, SecretKey aSecretKey ) {
    mySecretKeyStore.put(aUser, aSecretKey);
  }

  @Override
  public byte[] decryptMessageUsingSecretKey( byte[] aMessage ) throws EncryptionException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public byte[] encryptMessageForUserUsingSecretKey( String aUser, byte[] aMessage ) throws EncryptionException {
    // TODO Auto-generated method stub
    return null;
  }
  
  public synchronized void generateSecretKey(){
    //TODO implement
  }

  @Override
  public synchronized SecretKey getSecretKey() {
    if(mySecretKey == null){
      
    }
    return mySecretKey;
  }

}
