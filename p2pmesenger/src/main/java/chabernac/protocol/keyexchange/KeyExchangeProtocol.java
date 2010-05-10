/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import chabernac.encryption.EncryptionException;
import chabernac.encryption.iPublicPrivateKeyEncryption;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeException;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.tools.IOTools;

/**
 * 
 * This protocol must be able to retrieve the public key of another node in the network
 */
public class KeyExchangeProtocol extends Protocol implements iKeyProvider{
  public static final String ID = "KEP";

  private static Logger LOGGER = Logger.getLogger(KeyExchangeProtocol.class);

  public static enum Command{ GET_PUBLIC_KEY_ASYNC, GET_PUBLIC_KEY };
  public static enum Response{ UNKNOWN_COMMAND, OK, NOK };

  private iPublicPrivateKeyEncryption myEncryption = null;

  private iObjectStringConverter<PublicKey> myObjectConverter = new Base64ObjectStringConverter<PublicKey>();

  public KeyExchangeProtocol(iPublicPrivateKeyEncryption anEnctrypion){
    super(ID);
    myEncryption = anEnctrypion;
  }

  @Override
  public String getDescription() {
    return "Key Exchange Protocol";
  }

  private PipeProtocol getPipeProtocol() throws ProtocolException{
    PipeProtocol thePipeProtocol = (PipeProtocol)findProtocolContainer().getProtocol( "PIP" );
    thePipeProtocol.addPipeListener( new IncomingKeyListener() );
    return thePipeProtocol;
  }

  public iPublicPrivateKeyEncryption getEncryption(){
    return myEncryption;
  }


  @Override
  public String handleCommand( long aSessionId, String anInput ) {

    if( anInput.startsWith( Command.GET_PUBLIC_KEY_ASYNC.name() )){
      try{
        String thePeerName = anInput.split( " " )[1].trim();
        Pipe thePipe = getPipeProtocol().openPipe( thePeerName, "PUBLIC_KEY " + getPipeProtocol().getRoutingTable().getLocalPeerId() );
        ObjectOutputStream theObjectOutputStream = new ObjectOutputStream(thePipe.getSocket().getOutputStream());
        theObjectOutputStream.writeObject( myEncryption.getPublicKey() );
        BufferedReader theReader = new BufferedReader(new InputStreamReader(thePipe.getSocket().getInputStream()));
        String theResponse = theReader.readLine();
        return theResponse;
      }catch(Exception e){
        LOGGER.error("An error occured while sending public key", e);
        return Response.NOK.name();
      }
    } else if (anInput.equalsIgnoreCase(Command.GET_PUBLIC_KEY.name())){
      try {
        return myObjectConverter.toString(myEncryption.getPublicKey());
      } catch (Exception e) {
        return Response.NOK.name();
      }
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
  }

  /*
   * use getPublicKeyForUser instead 
   * @deprecated
   */
  public boolean assurePublicKeyForPeer(String aPeerId){
    if(myEncryption.getPublicKeyForUser( aPeerId ) != null){
      return true;
    }

    try{
      Message theMessage = new Message();
      theMessage.setDestination( getPipeProtocol().getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getPipeProtocol().getRoutingTable().obtainLocalPeer() );
      theMessage.setMessage( createMessage( Command.GET_PUBLIC_KEY_ASYNC.name() + " "  + getPipeProtocol().getRoutingTable().getLocalPeerId()));
      theMessage.setProtocolMessage( true );
      String theResponse = ((MessageProtocol)findProtocolContainer().getProtocol( "MSG" )).sendMessage( theMessage );
      if(!Response.OK.name().equalsIgnoreCase( theResponse )){
        throw new Exception("Response was not ok but '" + theResponse + "'");
      }
      return true;
    }catch(Exception e){
      LOGGER.error("Could not assure public key for peer '" + aPeerId +  "'", e);
      return false;
    }
  }

  private class IncomingKeyListener implements IPipeListener{

    @Override
    public void incomingPipe( Pipe aPipe ) throws PipeException {
      if(aPipe.getPipeDescription().startsWith( "PUBLIC_KEY" )){
        try{
          String thePeerId = aPipe.getPipeDescription().split( " " )[1];
          ObjectInputStream theInputStream = new ObjectInputStream(aPipe.getSocket().getInputStream());
          PublicKey thePublicKey = (PublicKey)theInputStream.readObject();
          myEncryption.storePublicKeyForUser( thePeerId, thePublicKey );
          PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(aPipe.getSocket().getOutputStream()));
          theWriter.println(Response.OK.name());
          theWriter.flush();
        } catch(Exception e){
          throw new PipeException("Unable to get public key out of pipe", e);
        }
      }
    }

  }

  @Override
  public PublicKey getPublicKeyForUser(String aUser) throws KeyException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( getPipeProtocol().getRoutingTable().getEntryForPeer( aUser ).getPeer() );
      theMessage.setSource( getPipeProtocol().getRoutingTable().obtainLocalPeer() );
      theMessage.setMessage( createMessage( Command.GET_PUBLIC_KEY.name()));
      theMessage.setProtocolMessage( true );
      String theResponse = ((MessageProtocol)findProtocolContainer().getProtocol( "MSG" )).sendMessage( theMessage );
      if(Response.NOK.name().equalsIgnoreCase( theResponse )){
        throw new KeyException("Unable to retrieve public key for user '" + aUser + "'");
      }
      return myObjectConverter.getObject(theResponse);
    } catch(Exception e){
      throw new KeyException("Could not get public key for user '" + aUser + "'");
    }
  }

  @Override
  public SecretKey getSecretKeyForUser(String aUser) throws KeyException{
    // TODO Auto-generated method stub
    return null;
  }

}
