/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.Peer;

public class EncryptionProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(EncryptionProtocol.class);
  public static final String ID = "ENC";
  public static enum Command{GENERATE_SECRET_KEY};
  public static enum Response{OK, NOK, INVALID_COMMAND};

  private iObjectStringConverter<PublicKey> myPublicKeyConverter = new Base64ObjectStringConverter< PublicKey >();

  private Map< String, SecretKey > myGeneratedKeysForSession = Collections.synchronizedMap( new HashMap< String, SecretKey> ());
  
  private KeyPair myKeyPair = null;

  public EncryptionProtocol ( ) throws EncryptionException {
    super( ID);
    generateKeyPair();
  }
  
  private void generateKeyPair() throws EncryptionException{
    try{
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);
    myKeyPair = keyGen.generateKeyPair();
    }catch(Exception e){
      throw new EncryptionException("Could not generate key pair", e);
    }
    
  }

  @Override
  public String getDescription() {
    return "Encryption Protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    if(anInput.startsWith( Command.GENERATE_SECRET_KEY.name() )){
      try{
        String[] theParams = anInput.split( " " );
        String theSession = theParams[1];
        PublicKey thePublicKey = myPublicKeyConverter.getObject( theParams[2] );
        SecretKey theSecretKey = generateSecretKey();
        //now store the secret key for this session
        myGeneratedKeysForSession.put(theSession, theSecretKey);

        byte[] theEncryptedSecretKey = encryptSecretKeyUsingPublicKey( theSecretKey, thePublicKey );
        return Response.OK + " " + new String(Base64.encodeBase64( theEncryptedSecretKey, false ));
      }catch(Exception e){
        LOGGER.error( "Could not generate secret key", e );
        return Response.NOK.name();
      }
    }
    return Response.INVALID_COMMAND.name();
  }

  private SecretKey getSecretKeyForPeer(Peer aPeer, String aSession) throws EncryptionException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( Command.GENERATE_SECRET_KEY.name()+ " " + aSession +  " " + myPublicKeyConverter.toString( myKeyPair.getPublic() )) );
      theMessage.setProtocolMessage( true );
      MessageProtocol theMessageProtocol = (MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID );
      String theResult = theMessageProtocol.sendMessage( theMessage );
      if(theResult.startsWith( Response.OK.name() )){
        byte[] theBase64EncryptedSecretKey = theResult.split( " " )[1].getBytes();
        byte[] theEncryptedSecretKey = Base64.decodeBase64( theBase64EncryptedSecretKey );
        return new SecretKeySpec(decrypteUsingPrivateKey( theEncryptedSecretKey, myKeyPair.getPrivate() ), "DES");
      }
      throw new EncryptionException("Invalid respons received when getting secret key '" + theResult + "'");
    }catch(Exception e){
      throw new EncryptionException("Could not get secret key for peer '" + aPeer + "' and session '" +  aSession + "'", e);
    }
  }

  private byte[] encryptSecretKeyUsingPublicKey(SecretKey aSecretKey, PublicKey aPublicKey) throws EncryptionException{
    try{
      Cipher theCipher = Cipher.getInstance ( aPublicKey.getAlgorithm () ) ;
      theCipher.init ( Cipher.ENCRYPT_MODE, aPublicKey) ;
      return theCipher.doFinal(  aSecretKey.getEncoded() );
    }catch(Exception e){
      throw new EncryptionException("Could not encrypt secret key using public key", e);
    }
  }
  
  private byte[] decrypteUsingPrivateKey(byte[] aBytes, PrivateKey aPrivateKey) throws EncryptionException{
    try{
      Cipher theCipher = Cipher.getInstance ( aPrivateKey.getAlgorithm () ) ;
      theCipher.init ( Cipher.DECRYPT_MODE, aPrivateKey) ;
      return theCipher.doFinal(  aBytes );
    }catch(Exception e){
      throw new EncryptionException("Could not decrypt bytes using private key", e);
    }
  }
  
  private byte[] encryptUsingSecretKey(SecretKey aSecretKey, byte[] aBytes) throws EncryptionException{
    try{
      Cipher theCipher = Cipher.getInstance ( aSecretKey.getAlgorithm () ) ;
      theCipher.init ( Cipher.ENCRYPT_MODE, aSecretKey) ;
      return theCipher.doFinal(  aBytes );
    }catch(Exception e){
      throw new EncryptionException("Could not encrypt bytes using secret key", e);
    }
  }
  
  private byte[] decryptUsingSecretKey(SecretKey aSecretKey, byte[] aBytes) throws EncryptionException{
    try{
      Cipher theCipher = Cipher.getInstance ( aSecretKey.getAlgorithm () ) ;
      theCipher.init ( Cipher.DECRYPT_MODE, aSecretKey) ;
      return theCipher.doFinal(  aBytes );
    }catch(Exception e){
      throw new EncryptionException("Could not encrypt bytes using secret key", e);
    }
  }

  private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
    keyGen.init(new SecureRandom());
    return keyGen.generateKey();
  }
  
  public void encryptMessage(Message aMessage) throws EncryptionException{
    String theSession = UUID.randomUUID().toString();
    SecretKey theSecretKey = getSecretKeyForPeer( aMessage.getDestination(), theSession );
    aMessage.addHeader( "session", theSession );
    aMessage.setMessage( new String(Base64.encodeBase64( encryptUsingSecretKey( theSecretKey, aMessage.getMessage().getBytes() ), false)));
    aMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
  }
  
  public void decrypteMessage(Message aMessage) throws EncryptionException{
    String theSession = aMessage.getHeader( "session" );
    //the session can only be used once, so we remove it emmediately
    SecretKey theSecretKey = myGeneratedKeysForSession.remove( theSession );
    byte[] theBytes = decryptUsingSecretKey( theSecretKey, Base64.decodeBase64( aMessage.getMessage().getBytes() ));
    aMessage.setMessage( new String(theBytes) );
    aMessage.removeHeader( "session" );
    aMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
  }

  @Override
  public void stop() {

  }

}
