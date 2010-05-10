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

public class KeyStore {

  private File myKeyLocationDirectory = null;
  private KeyPair myKeyPair = null;
  private String myUser = null;
  
  private Map<String, PublicKey > myPublicKeyStore = Collections.synchronizedMap( new HashMap<String, PublicKey >());
  
  public KeyStore(File aKeyLocationDirectory, String aUser) throws EncryptionException{
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


  public PublicKey getPublicKeyForUser( String aUser ) {
    return myPublicKeyStore.get(aUser);
  }

  public void storePublicKeyForUser( String aUser, PublicKey aKey ) {
    myPublicKeyStore.put(aUser, aKey);
  }

  public PublicKey getPublicKey() throws EncryptionException{
    if(myKeyPair == null ) throw new EncryptionException("No key present");
    return myKeyPair.getPublic();
  }

  public String getUser() {
    return myUser;
  }

}
