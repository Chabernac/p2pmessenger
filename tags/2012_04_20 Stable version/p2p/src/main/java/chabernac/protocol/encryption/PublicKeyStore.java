/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class PublicKeyStore {
  private static Logger LOGGER = Logger.getLogger( PublicKeyStore.class );
  
  private Map<String, PublicKey> myKeys = new HashMap<String, PublicKey>();
  
  private final File myKeyStoreLocation;
  private final boolean isSaveOnUpdate;
  
  public PublicKeyStore( File aKeyStoreLocation, boolean isSaveOnUpdate ) {
    super();
    myKeyStoreLocation = aKeyStoreLocation;
    this.isSaveOnUpdate = isSaveOnUpdate;
  }

  public synchronized void storeKey(String aPeerId, PublicKey akey){
    myKeys.put(aPeerId, akey);
    if(isSaveOnUpdate){
      store();
    }
  }
  
  public synchronized PublicKey getKey(String aPeerId) throws KeyNotFoundException{
    if(!myKeys.containsKey( aPeerId )) throw new KeyNotFoundException("A public key for peer '" + aPeerId + "' could not be found");
    return myKeys.get(aPeerId);
  }
  
  public synchronized boolean containsKeyFor(String aPeerId){
    return myKeys.containsKey( aPeerId );
  }
  
  public void store(){
    ObjectOutputStream theStream = null;
    try{
      theStream = new ObjectOutputStream(new FileOutputStream( myKeyStoreLocation ));
      theStream.writeObject( myKeys );
    } catch (Exception e){
      LOGGER.error( "Could not store key store", e );
    } finally {
      if(theStream != null){
        try {
          theStream.close();
        } catch ( IOException e ) {
        }
      }
    }
  }
  
  public void load(){
    if(!myKeyStoreLocation.exists()) return;
    
    ObjectInputStream theInputStream = null;
    try{
      theInputStream = new ObjectInputStream(new FileInputStream( myKeyStoreLocation ));
      myKeys = (Map<String, PublicKey>)theInputStream.readObject();
    } catch(Exception e){
      LOGGER.error( "Could not load key store", e );
    } finally {
      if(theInputStream != null){
        try {
          theInputStream.close();
        } catch ( IOException e ) {
        }
      }
    }
  }
  
}
