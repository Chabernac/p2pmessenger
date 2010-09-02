/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import chabernac.protocol.ProtocolMessageEntry.Status;
import chabernac.protocol.routing.WhoIsRunningTableModel.Entry.State;

public class ProtocolContainer implements IProtocol {
  public static enum Command {PROTOCOLS};
  public static enum Response {UNKNOWN_COMMAND, UNKNOWN_PROTOCOL, INVALID_PROTOCOL};
  
  private Map<String, IProtocol> myProtocolMap = null;
  private List< ProtocolMessageEntry > myMessageHistory = Collections.synchronizedList( new ArrayList< ProtocolMessageEntry >() );
  private List<iProtocolMessageListener> myListeners = new ArrayList< iProtocolMessageListener >();
  
  private iProtocolFactory myProtocolFactory = null;
  private ServerInfo myServerInfo = null;
  
  public ProtocolContainer(iProtocolFactory aProtocolFactory){
    addProtocol( this );
    myProtocolFactory = aProtocolFactory;
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    ProtocolMessageEntry theEntry = new ProtocolMessageEntry(anInput, Status.INPROGRESS);
    myMessageHistory.add( theEntry );
    notifyListeners();
    
    if(anInput.length() < 3) {
      theEntry.setStatus( Status.INVALID );
      return Response.INVALID_PROTOCOL.name();
    }
    String theID = anInput.substring( 0, 3 );
    IProtocol theProtocol;
    try {
      theProtocol = getProtocol( theID );
      String theResult = theProtocol.handleCommand( aSessionId, anInput.substring( 3 ) );
      theEntry.setOutput( theResult );
      theEntry.setStatus(Status.FINISHED);
      notifyListeners();
      return theResult;
    } catch ( ProtocolException e ) {
      theEntry.setStatus( Status.INVALID );
      notifyListeners();
      return Response.UNKNOWN_PROTOCOL.name();
    }
  }
  
  private void notifyListeners(){
   for(iProtocolMessageListener theListener : myListeners){
     theListener.messageReceived();
   }
  }
  
  public String getProtocolString() {
    StringBuilder theBuilder = new StringBuilder();
    for(String theId : myProtocolMap.keySet()){
      theBuilder.append( theId );
      theBuilder.append( ";" );
    }
    return theBuilder.toString();
  }

  private void addProtocol(IProtocol aProtocol) {
    if(aProtocol.getId() == null){
      throw new IllegalArgumentException("Can only add a sub protocol which has an id");
    }
    
    if(myProtocolMap == null){
      myProtocolMap = Collections.synchronizedMap( new HashMap< String, IProtocol > ());
    }
    
    if(myProtocolMap.containsKey( aProtocol.getId() )){
      throw new IllegalArgumentException("a subprotocol with this id is already registered");
    }
    
    myProtocolMap.put( aProtocol.getId(), aProtocol);
    aProtocol.setMasterProtocol( this );
  }

  @Override
  public String getId() {
    return "MAS";
  }

  @Override
  public void setMasterProtocol( IProtocol aProtocol ) {
  }

  @Override
  public synchronized void stop() {
    ExecutorService theExecutorService = Executors.newCachedThreadPool();
    //let's stop each protocol in a seperate thread to speed it up.
    for(final IProtocol theProtocol : myProtocolMap.values()){
      if(theProtocol != this){
        theExecutorService.execute( new Runnable(){
          public void run(){
            theProtocol.stop();
          }
        });
      }
    }
    //now stop after 5 seconds, we don't want to block the entire system.
    try {
      theExecutorService.shutdown();
      theExecutorService.awaitTermination( 5, TimeUnit.SECONDS );
    } catch ( InterruptedException e ) {
    }
  }
  
  public synchronized IProtocol getProtocol(String anId) throws ProtocolException{
    if(!myProtocolMap.containsKey( anId )){
      IProtocol theProtocol = myProtocolFactory.createProtocol( anId );
      if(myServerInfo != null){
        theProtocol.setServerInfo( myServerInfo );
      }
      addProtocol( theProtocol );
    }
    IProtocol theProtocol = myProtocolMap.get( anId );
    
    return theProtocol;
  }

  @Override
  public void setServerInfo( ServerInfo aServerInfo ) throws ProtocolException {
    myServerInfo = aServerInfo;
    for(Iterator< IProtocol > i = new ArrayList<IProtocol>(myProtocolMap.values()).iterator();i.hasNext();){
      IProtocol theProtocol = i.next();
      if(theProtocol != this){
        theProtocol.setServerInfo( myServerInfo );
      }
    }
  }
  
  public void addProtocolMessageListener(iProtocolMessageListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeProtocolMessageListener(iProtocolMessageListener aListener){
    myListeners.remove( aListener );
  }
  
  public List<ProtocolMessageEntry> getMessageHistory() {
    return Collections.unmodifiableList(myMessageHistory);
  }
}
