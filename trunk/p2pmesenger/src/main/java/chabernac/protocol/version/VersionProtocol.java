/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.version;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class VersionProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(VersionProtocol.class);
  public static enum Command{VERSION};
  public static enum Response{OK, UNKNOWN_COMMAND};
  public static String ID = "VER";

  private Version myVersion = new Version("0.0.1");

  private Map<String, Version> myVersions = Collections.synchronizedMap( new HashMap< String, Version >() );

  private ExecutorService myService = Executors.newFixedThreadPool( 5 );
  
  private Set< VersionListener > myVersionsListeners = new HashSet< VersionListener >();

  public VersionProtocol ( Version aLocalVersion ) {
    super( ID );
    myVersion = aLocalVersion;
  }

  public void setMasterProtocol( IProtocol aProtocol ) {
    super.setMasterProtocol( aProtocol );

    try{
      fullRetrieval();
      addListeners();
    }catch(Exception e){
      LOGGER.error( "Could not fully initialize UserInfoProtocol", e );
    }
  }

  @Override
  public String getDescription() {
    return "Version protocol";
  }

  public void addListeners() throws ProtocolException{
    getRoutingTable().addRoutingTableListener( new MyRoutingTableListener() );
  }
  
  public void addVersionListener(VersionListener aVersionListener){
    myVersionsListeners.add( aVersionListener );
  }
  
  private void notifyListeners(String aPeer, Version aVersion) {
    for(VersionListener theListener : myVersionsListeners){
      theListener.versionChanged( aPeer, aVersion, Collections.unmodifiableMap( myVersions ) );
    }
  }
  
  public void removeVersionListener(VersionListener aVersionListener){
    myVersionsListeners.remove( aVersionListener );
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    if(anInput.equalsIgnoreCase( Command.VERSION.name() )){
      return Response.OK.name() + myVersion.toString();
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  public void fullRetrieval() throws VersionProtocolException{
    try{
      RoutingTable theRoutingTable = getRoutingTable();

      for(RoutingTableEntry theEntry : theRoutingTable.getEntries()){
        if(!myVersions.containsKey( theEntry.getPeer().getPeerId() )){
          getVersionForPeer( theEntry.getPeer().getPeerId() );
        }
      }
    }catch(Exception e){
      throw new VersionProtocolException("Could not retrieve highest version", e);
    }
  }

  public Version getLocalVersion(){
    return myVersion;
  }

  public Map<String, Version> getVersions(){
    return Collections.unmodifiableMap( myVersions );
  }

  private void getVersionForPeer(final String aPeerId){
    myService.execute( new Runnable(){
      public void run(){
        try{
          RoutingTableEntry theEntry = getRoutingTable().getEntryForPeer( aPeerId );

          Message theMessage = new Message();
          theMessage.setDestination( theEntry.getPeer() );
          theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
          theMessage.setProtocolMessage( true );
          theMessage.setMessage( createMessage( Command.VERSION.name() ) );
          String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).sendMessage( theMessage );
          if(theResult.startsWith( Response.OK.name() )){
            Version theVersion = new Version(theResult.substring( Response.OK.name().length() ));
            myVersions.put(aPeerId, theVersion);
            notifyListeners(aPeerId, theVersion);
          }
          throw new VersionProtocolException("Received invalid response from version protocol '" + theResult + "'");
        }catch(Exception e){
          LOGGER.error("Unable to retrieve version for peer '" + aPeerId + "'");
        }
      }
    });

  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

  private class MyRoutingTableListener implements IRoutingTableListener {

    @Override
    public void routingTableEntryChanged( final RoutingTableEntry anEntry ) {
      getVersionForPeer( anEntry.getPeer().getPeerId() );
    }

  }

}
