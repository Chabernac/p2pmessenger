/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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
    private static final String SECRET_KEY_ALGORITHM = "DES";
    public static final String ID = "ENC";
    public static enum Command{ GET_PUBLIC_KEY};
    public static enum Response{OK, NOK, INVALID_COMMAND};
    private final String LOCK_PREFIX = UUID.randomUUID().toString();

    private iObjectStringConverter<PublicKey> myPublicKeyConverter = new Base64ObjectStringConverter< PublicKey >();

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
        if(anInput.startsWith( Command.GET_PUBLIC_KEY.name() )){
            try {
                LOGGER.debug("Returning public key with hash '" + convertBytesToString(calculateHash(myKeyPair.getPublic().getEncoded())));
                return Response.OK +  " " + myPublicKeyConverter.toString(myKeyPair.getPublic());
            } catch (Exception e) {
                LOGGER.error("Unable to convert public key to string", e);
                return Response.NOK.name();
            }
        } 
        return Response.INVALID_COMMAND.name();
    }

    private byte[] encryptSecretKeyUsingPublicKey(SecretKey aSecretKey, PublicKey aPublicKey) throws EncryptionException{
        return encryptUsingPublicKey( aSecretKey.getEncoded(), aPublicKey );
    }

    private byte[] decryptUsingPrivateKey(byte[] aBytes, PrivateKey aPrivateKey) throws EncryptionException{
        return decryptEncrypt( aBytes, aPrivateKey, Cipher.DECRYPT_MODE );
    }
    
    private byte[] decryptUsingPublicKey(byte[] aBytes, PublicKey aPrivateKey) throws EncryptionException{
        return decryptEncrypt( aBytes, aPrivateKey, Cipher.DECRYPT_MODE );
    }
    
    private byte[] encryptUsingPrivateKey(byte[] aBytes, PrivateKey aPrivateKey) throws EncryptionException{
        return decryptEncrypt( aBytes, aPrivateKey, Cipher.ENCRYPT_MODE );
    }
    
    private byte[] encryptUsingPublicKey(byte[] aBytes, PublicKey aPrivateKey) throws EncryptionException{
        return decryptEncrypt( aBytes, aPrivateKey, Cipher.ENCRYPT_MODE );
    }
    
    
    
    private byte[] decryptEncrypt(byte[] aBytes, Key aKey, int aMode) throws EncryptionException{
        try{
            Cipher theCipher = Cipher.getInstance ( aKey.getAlgorithm () ) ;
            theCipher.init ( aMode, aKey) ;
            return theCipher.doFinal(  aBytes );
        }catch(Exception e){
            throw new EncryptionException("Could not decrypt or encrypt bytes", e);
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

    private SecretKey generateSecretKey(String anAlgorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(anAlgorithm);
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

    private byte[] calculateHash(byte[] aBytes) throws NoSuchAlgorithmException{
        MessageDigest theDigest = MessageDigest.getInstance("SHA-1");
        theDigest.update(aBytes);
        return theDigest.digest();
    }

    public void encryptMessage(Message aMessage) throws EncryptionException{
        UUID theMessageId = aMessage.getMessageId();
        PublicKey thePublicKey = getPublicKeyFor(aMessage.getDestination(), false);

        //using the public key of this peer encrypt a secret key and store it in the header of the message
        try{
            byte[] theMessageHash = calculateHash(aMessage.getMessage().getBytes());
            
            SecretKey theSecretKey = generateSecretKey(SECRET_KEY_ALGORITHM);
            byte[] theEncryptedSecretKey = encryptSecretKeyUsingPublicKey( theSecretKey, thePublicKey );
            
            //add the alogorithm used for generating the secret key
            aMessage.addHeader("SECRET_KEY_ALGORITHM", SECRET_KEY_ALGORITHM);
            
            //store the encrypted secret key in the header
            aMessage.addHeader("SECRET_KEY", convertBytesToString(theEncryptedSecretKey));
            
            //calculate a hash of the original message which the receiver can use to verify the message
            aMessage.addHeader("MESSAGE_HASH", convertBytesToString(theMessageHash));
            
            //encrypt the hash of the message with the local public key and add it to the message
            //this way the receiver can verify that the message was send by the right peer
            aMessage.addHeader("ENCRYPTED_MESSAGE_HASH", convertBytesToString(encryptUsingPrivateKey( theMessageHash, myKeyPair.getPrivate())));
            
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
        aMessage.setMessageId(theMessageId);
    }

    public void decryptMessage(Message aMessage) throws EncryptionException{
        UUID theMessageId = aMessage.getMessageId();

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
            SecretKey theSecretKey = new SecretKeySpec( decryptUsingPrivateKey(theEncryptedSecretKey, myKeyPair.getPrivate()), aMessage.getHeader( "SECRET_KEY_ALGORITHM", "DES" ));

            //now we can use the secret key to decrypt the actual message
            byte[] theDecryptedMessage = decryptUsingSecretKey(theSecretKey, convertStringToBytes(aMessage.getMessage()));
            String theMessage = new String(theDecryptedMessage);

            //calculate the hash and check it is them same as the given one
            String theHash = convertBytesToString(calculateHash(theMessage.getBytes()));

            if(!theHash.equals(aMessage.getHeader("MESSAGE_HASH"))){
                throw new EncryptionException("Could not decrypt message, given hash and calculated hash do not match");
            }
            
            //now decrypt the hash using the public key of the sender, and verify the hahs
            //this will assure us that the message was send by the right peer
            if(aMessage.containsHeader( "ENCRYPTED_MESSAGE_HASH" )){
                String theDecryptedHash = "not decrypted";
                try{
                    theDecryptedHash = convertBytesToString( decryptUsingPublicKey( convertStringToBytes( aMessage.getHeader( "ENCRYPTED_MESSAGE_HASH" )),getPublicKeyFor( aMessage.getSource(), false)));
                }catch(EncryptionException e){
                    LOGGER.info( "Unable to decrypte message using stored public key", e );
                }
                if(!theHash.equals(theDecryptedHash)){
                    //maybe we did not have the right public key of the other peer, force an update
                    try{
                        theDecryptedHash = convertBytesToString( decryptUsingPublicKey( convertStringToBytes( aMessage.getHeader( "ENCRYPTED_MESSAGE_HASH" )), getPublicKeyFor( aMessage.getSource(), true) ));
                    }catch(EncryptionException e){
                        LOGGER.info( "Unable to decrypte message using obtained public key", e );
                    }
                    if(!theHash.equals(theDecryptedHash)){
                        throw new EncryptionException("The hash of the message was not encrypted with the right private key");
                    }
                }   
            }
            
            aMessage.setMessage(theMessage);
            aMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
        }catch(Exception e){
            LOGGER.error("An error occured while decrypting message", e);
            throw new EncryptionException("Could not decrypt message", e);
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
