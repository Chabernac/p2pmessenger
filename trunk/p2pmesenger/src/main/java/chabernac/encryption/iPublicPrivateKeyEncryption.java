/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.encryption;

import java.security.PublicKey;

import javax.crypto.SecretKey;

public interface iPublicPrivateKeyEncryption {
  
  /**
   * Store the public key for this user
   * 
   * @param aUser
   * @param aKey
   */
  public void storePublicKeyForUser(String aUser, PublicKey aKey);
  
  /**
   * return the public key for this user
   * @param aUser
   * @return
   */
  public PublicKey getPublicKeyForUser(String aUser);
  
  /**
   * store the secret key for this user
   * 
   * @param aUser
   * @param akey
   */
  public void storeSecretKeyForUser(String aUser, SecretKey akey);
  
  /**
   * return the secret key for this user
   * 
   * @param aUser
   * @return
   */
  public SecretKey getSecretKeyForUser(String aUser);
  
  /**
   * Encrypt the given message for this user with the public key we have for this user
   * throw an encryption exception if we do not have the public key of this user. 
   * @param aUser
   * @param aMessage
   * @return
   */
  public byte[] encryptMessageForUser(String aUser, byte[] aMessage) throws EncryptionException;
  
  /**
   * Decrypt the given message using our own private key.
   * (The private key should be stored inside the implementing object and stored in a save place where it can be retrieved)
   * @param aMessage
   * @return
   */
  public byte[] decryptMessage(byte[] aMessage) throws EncryptionException;
  
  public String getUser();
  
  public PublicKey getPublicKey() throws EncryptionException;
  
  public SecretKey getSecretKey();
  
  public byte[] encryptMessageForUserUsingSecretKey(String aUser, byte[] aMessage) throws EncryptionException;
  
  public byte[] decryptMessageUsingSecretKey(byte[] aMessage) throws EncryptionException;
}
