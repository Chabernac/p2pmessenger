/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
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
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException.Reason;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class EncryptionProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(EncryptionProtocol.class);
  public static final String ID = "ENC";
  public static enum Command{GENERATE_SECRET_KEY, GET_PUBLIC_KEY, PUT_PUBLIC_KEY};
  public static enum Response{OK, NOK, INVALID_COMMAND};
  private final String LOCK_PREFIX = UUID.randomUUID().toString();

  private iObjectStringConverter<PublicKey> myPublicKeyConverter = new Base64ObjectStringConverter< PublicKey >();

  private Map< String, SecretKey > myGeneratedKeysForSession = Collections.synchronizedMap( new HashMap< String, SecretKey> ());

  private PublicKeyStore myPublicKeys = new PublicKeyStore( new File("publicKeyStore.bin"), true);

  private KeyPair myKeyPair = null;

  public EncryptionProtocol ( ) throws EncryptionException {
    super( ID);
    generateKeyPair();
    loadKeyStore();
  }

  public void generateKeyPair() throws EncryptionException{
    try{
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      keyGen.initialize(1024, random);
      myKeyPair = keyGen.generateKeyPair();
    }catch(Exception e){
      throw new EncryptionException("Could not generate key pair", e);
    }
  }

  private void loadKeyStore(){
    myPublicKeys.load();
  }

  @Override
  public String getDescription() {
    return "Encryption Protocol";
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
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
    } else if(anInput.startsWith( Command.GET_PUBLIC_KEY.name() )){
      try {
        LOGGER.debug("Returning public key with hash '" + convertBytesToString(calculateHash(myKeyPair.getPublic().getEncoded())));
        return Response.OK +  " " + myPublicKeyConverter.toString(myKeyPair.getPublic());
      } catch (Exception e) {
        LOGGER.error("Unable to convert public key to string", e);
        return Response.NOK.name();
      }
    } else if(anInput.startsWith( Command.PUT_PUBLIC_KEY.name() )){
      try{
        String[] theParts = anInput.split( " " );
        String thePeerId = theParts[1];
        PublicKey thePublicKey = myPublicKeyConverter.getObject( theParts[2] );
        myPublicKeys.storeKey( thePeerId, thePublicKey);
        return Response.OK.name();
      }catch(Exception e){
        LOGGER.error("Could not store public key", e);
        return Response.NOK.name();
      }
    }
    return Response.INVALID_COMMAND.name();
  }

  private SecretKey getSecretKeyForPeer(AbstractPeer aPeer, String aSession) throws EncryptionException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( Command.GENERATE_SECRET_KEY.name() + " " + aSession +  " " + myPublicKeyConverter.toString( myKeyPair.getPublic() )) );
      theMessage.setProtocolMessage( true );
      AsyncMessageProcotol theMessageProtocol = (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID );
      String theResult = theMessageProtocol.sendAndWaitForResponse( theMessage );
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

  private String convertBytesToString(byte[] aBytes){
    return new String(Base64.encodeBase64(aBytes, false));
  }

  private byte[] convertStringToBytes(String aString){
    return Base64.decodeBase64(aString.getBytes());
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

  public PublicKey getPublicKeyFor(AbstractPeer aPeer, boolean isForceUpdate) throws EncryptionException{
    //synchronize on the peer id so that we do not try to obtain the public key while it is already being requested
    synchronized(LOCK_PREFIX + aPeer.getPeerId()){
      try{
        if(isForceUpdate || !myPublicKeys.containsKeyFor( aPeer.getPeerId())){
          Message theMessage = new Message();
          theMessage.setDestination( aPeer );
          theMessage.setMessage( createMessage( Command.GET_PUBLIC_KEY.name() ));
          theMessage.setProtocolMessage( true );
          AsyncMessageProcotol theMessageProtocol = (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID );
          String theResult = theMessageProtocol.sendAndWaitForResponse(theMessage );
          if(theResult.startsWith( Response.OK.name() )){
            myPublicKeys.storeKey( aPeer.getPeerId(), myPublicKeyConverter.getObject(theResult.split(" ")[1]));
          }
        }
        return myPublicKeys.getKey( aPeer.getPeerId());
      }catch(Exception e){
        LOGGER.error("An error occured while getting public key for peer '" + aPeer.getPeerId() + "'", e);
        throw new EncryptionException("Could not get public key for peer '" + aPeer.getPeerId() + "'", e);
      }
    }
  }

  private void sendPublicKeyTo(AbstractPeer aPeer) throws EncryptionException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( Command.PUT_PUBLIC_KEY.name() + " " + 
          getRoutingTable().getEntryForLocalPeer().getPeer().getPeerId() + " " + 
          myPublicKeyConverter.toString( myKeyPair.getPublic() )));
      theMessage.setProtocolMessage( true );
      AsyncMessageProcotol theMessageProtocol = (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID );
      String theResult = theMessageProtocol.sendAndWaitForResponse(theMessage );
      if(!theResult.startsWith( Response.OK.name() )){
        throw new EncryptionException("Invalid response received on send public key '" + theResult + "'");
      }
    }catch(Exception e){
      LOGGER.error("An error occured while sending public key to peer '" + aPeer.getPeerId() + "'", e);
      throw new EncryptionException("Could not send public key to peer '" + aPeer.getPeerId() + "'", e);
    }
  }

  private byte[] calculateHash(byte[] aBytes) throws NoSuchAlgorithmException{
    MessageDigest theDigest = MessageDigest.getInstance("SHA-1");
    theDigest.update(aBytes);
    return theDigest.digest();
  }

  public void encryptMessage(Message aMessage) throws EncryptionException{
    UUID theMessageId = aMessage.getMessageId();
    PublicKey thePublicKey = getPublicKeyFor(aMessage.getDestination(), false);

    if(thePublicKey == null){
      //this peer does not use the new way of encryption yet, fall back to the old one
      String theSession = UUID.randomUUID().toString();
      SecretKey theSecretKey = getSecretKeyForPeer( aMessage.getDestination(), theSession );
      aMessage.addHeader( "session", theSession );
      aMessage.setMessage( new String(Base64.encodeBase64( encryptUsingSecretKey( theSecretKey, aMessage.getMessage().getBytes() ), false)));
      aMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
      aMessage.removeMessageIndicator(MessageIndicator.TO_BE_ENCRYPTED);
    } else {
      //using the public key of this peer encrypt a secret key and store it in the header of the message
      try{
        SecretKey theSecretKey = generateSecretKey();
        byte[] theEncryptedSecretKey = encryptSecretKeyUsingPublicKey( theSecretKey, thePublicKey );
        //store the encrypted secret key in the header
        aMessage.addHeader("SECRET_KEY", convertBytesToString(theEncryptedSecretKey));
        //calculate a hash of the original message which the receiver can use to verify the message
        aMessage.addHeader("MESSAGE_HASH", convertBytesToString(calculateHash(aMessage.getMessage().getBytes())));
        //add a hash of the public key used so that the receiver can verify if the correct public key was used
        String theHashOfPublicKey = convertBytesToString(calculateHash(thePublicKey.getEncoded()));
        //        LOGGER.debug("Adding hash of public key to message '" + theHashOfPublicKey + "'");
        aMessage.addHeader("PUBLIC_KEY_HASH", theHashOfPublicKey);

        //now encrypt the message using the secret key
        aMessage.setMessage(convertBytesToString(encryptUsingSecretKey(theSecretKey, aMessage.getMessage().getBytes())));
        aMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
        aMessage.removeMessageIndicator(MessageIndicator.TO_BE_ENCRYPTED);
      }catch(Exception e){
        LOGGER.error("An error occured while encrypting message", e);
        throw new EncryptionException("Could not encrypt message", e);
      }
    }
    aMessage.setMessageId(theMessageId);
  }


  public void decryptMessage(Message aMessage) throws EncryptionException{
    UUID theMessageId = aMessage.getMessageId();
    if(aMessage.getHeader("SECRET_KEY") == null){
      //the message was encrypted using the old way
      String theSession = aMessage.getHeader( "session" );
      //the session can only be used once, so we remove it emmediately
      SecretKey theSecretKey = myGeneratedKeysForSession.remove( theSession );
      byte[] theBytes = decryptUsingSecretKey( theSecretKey, Base64.decodeBase64( aMessage.getMessage().getBytes() ));
      aMessage.setMessage( new String(theBytes) );
      aMessage.removeHeader( "session" );
      aMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
    } else {
      try{
        //first check if the public key used was the correct one
        String theMyPublicKey = convertBytesToString(calculateHash(myKeyPair.getPublic().getEncoded()));
        if(!theMyPublicKey.equals(aMessage.getHeader("PUBLIC_KEY_HASH"))){
          //          sendPublicKeyTo(aMessage.getSource());
          LOGGER.error("The hash of the public key used for encryption of the secret key '" + aMessage.getHeader("PUBLIC_KEY_HASH") + "' is not the same as the local hash of the pulic key '" + theMyPublicKey + "'");
          throw new EncryptionException(Reason.ENCRYPTED_USING_BAD_PUBLIC_KEY, "The message was encrypted using a bad or old public key");
        }

        //obtain the secret key by decrypting it with our own private key
        byte[] theEncryptedSecretKey = convertStringToBytes(aMessage.getHeader("SECRET_KEY"));
        SecretKey theSecretKey = new SecretKeySpec( decrypteUsingPrivateKey(theEncryptedSecretKey, myKeyPair.getPrivate()), "DES");

        //now we can use the secret key to decrypt the actual message
        byte[] theDecryptedMessage = decryptUsingSecretKey(theSecretKey, convertStringToBytes(aMessage.getMessage()));
        String theMessage = new String(theDecryptedMessage);

        //calculate the has and check it is them same as the given one
        String theHash = convertBytesToString(calculateHash(theMessage.getBytes()));

        if(!theHash.equals(aMessage.getHeader("MESSAGE_HASH"))){
          throw new EncryptionException("Could not decrypt message, given hash and calculated hash do not match");
        }
        aMessage.setMessage(theMessage);
        aMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
      }catch(Exception e){
        LOGGER.error("An error occured while decrypting message", e);
        throw new EncryptionException("Could not decrypt message", e);
      }
    }
    aMessage.setMessageId( theMessageId );
  }

  void setPublicKeyFor(String aPeer, PublicKey aKey){
    myPublicKeys.storeKey( aPeer, aKey);
  }

  PublicKey getPublicKey(){
    return myKeyPair.getPublic();
  }

  @Override
  public void stop() {

  }

}
