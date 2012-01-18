/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.utils.NamedRunnable;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 7-jul-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      7-jul-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac</a>
 */
public class InfoExchangeProtocol<T extends Observable & Serializable> extends Protocol {
  public static String ID = "INF";
  private static Logger LOGGER = Logger.getLogger( InfoExchangeProtocol.class );

  private final T myInformationObject;
  private MyInformationObjectObserver myObserver = new MyInformationObjectObserver();
  private iObjectStringConverter<T> myObjectPersister = new Base64ObjectStringConverter< T >();
  private Map<String, T> myInfoMap = Collections.synchronizedMap( new HashMap< String, T > ());
  private Set<iInfoListener<T>> myInfoListeners = new HashSet< iInfoListener< T >>();

//  private ExecutorService myService = DynamicSizeExecutor.getSmallInstance();

  private enum Command{PUT, GET};
  private enum Response{OK, NOK, UNKNOWN_COMMAND};

  public InfoExchangeProtocol ( T anInformationObject ) {
    this(ID, anInformationObject);
  }

  public InfoExchangeProtocol ( String anId, T anInformationObject ) {
    super( anId );
    myInformationObject = anInformationObject;
    myInformationObject.addObserver( myObserver );
  }

  @Override
  public String getDescription() {
    return "Information exchange protocol";
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    try{
      if(anInput.startsWith( Command.PUT.name() )){
        String theParts[] = anInput.substring( Command.PUT.name().length() ).trim().split( " " );
        if(theParts.length != 2)  {
          LOGGER.error("The number of parts in the input string is not 2 '" + anInput + "'");
          return Response.NOK.name();
        }

        String thePeerId = theParts[0];
        T theInfoObject = (T)myObjectPersister.getObject( theParts[1] );
        storeInfo(thePeerId, theInfoObject);
        return Response.OK.name();
      } else if(anInput.startsWith(Command.GET.name())){
        return myObjectPersister.toString(myInformationObject);
      }
    }catch(Exception e){
      LOGGER.error("An error occured while handling command", e);
      return Response.NOK.name();
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  private void storeInfo( String anPeerId, T anInfoObject ) {
    myInfoMap.put(anPeerId, anInfoObject);
    for(iInfoListener< T > theListener : myInfoListeners){
      theListener.infoChanged( anPeerId, Collections.unmodifiableMap( myInfoMap  ) );
    }
  }
  
  public Map<String, T> getInfoMap(){
    return Collections.unmodifiableMap( myInfoMap );
  }

  @Override
  public void stop() {
    myInformationObject.deleteObserver( myObserver );
  }

  public void setMasterProtocol( IProtocol aProtocol ) {
    super.setMasterProtocol( aProtocol );

    try{
      pushInformationToAllPeers();
      addListeners();
    }catch(Exception e){
      LOGGER.error( "Could not fully initialize UserInfoProtocol", e );
    }
  }

  public void addInfoListener(iInfoListener<T> anInfoListener){
    myInfoListeners.add( anInfoListener );
  }

  public void removeInfoListener( iInfoListener< InfoObject > anListener ) {
    myInfoListeners.remove( anListener );
  }

  private void pushInformationToAllPeers() {
    try{
      RoutingTable theTable = getRoutingTable();
      for(RoutingTableEntry theEntry : theTable){
        if(theEntry.isReachable()){
          getExecutorService().execute( new SendInfoToPeer(theEntry.getPeer().getPeerId()));
        }
      }
    }catch(Exception e){
      LOGGER.error( "Unable to push information to all peers", e );
    }
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  public void addListeners() throws ProtocolException{
    getRoutingTable().addRoutingTableListener( new MyRoutingTableListener() );
  }

  public T getInfoObject(){
    return myInformationObject;
  }


  private class MyInformationObjectObserver implements Observer {
    @Override
    public void update( Observable anO, Object anArg ) {
      pushInformationToAllPeers();
    }
  }

  private class GetInfoFromPeer extends NamedRunnable{
    private final String myPeerId;
    
    public GetInfoFromPeer(String aPeerId){
      myPeerId = aPeerId;
    }
    
    public void doRun(){
      if(!myInfoMap.containsKey(myPeerId)){
        try{
          RoutingTableEntry theEntry = getRoutingTable().getEntryForPeer( myPeerId );

          if(theEntry.isReachable() && 
              theEntry.getPeer().isOnSameChannel(getRoutingTable().getEntryForLocalPeer().getPeer()) &&
              theEntry.getPeer().isProtocolSupported( ID )            
              ){
            Message theMessage = new Message();
            theMessage.setDestination( theEntry.getPeer() );
            theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
            theMessage.setProtocolMessage( true );
            theMessage.setMessage( createMessage( Command.GET.name() ));
            String theResult = ((AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID )).sendAndWaitForResponse(theMessage );
            T theInfoObject = myObjectPersister.getObject(theResult);
            storeInfo(myPeerId, theInfoObject);
          }
        }catch(Exception e){
          LOGGER.error("An error occured while sending message to peer '" + myPeerId + "'", e);
        }
      }
    }
  }
  
  private class SendInfoToPeer extends NamedRunnable{
    private final String myPeerId;

    public SendInfoToPeer(String aPeerId){
      myPeerId = aPeerId;
    }

    @Override
    public void doRun() {
      try{
        RoutingTableEntry theEntry = getRoutingTable().getEntryForPeer( myPeerId );

        if(theEntry.isReachable() && 
            theEntry.getPeer().isOnSameChannel(getRoutingTable().getEntryForLocalPeer().getPeer()) &&
            theEntry.getPeer().isProtocolSupported( ID )            
            ){
          Message theMessage = new Message();
          theMessage.setDestination( theEntry.getPeer() );
          theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
          theMessage.setProtocolMessage( true );
          theMessage.setMessage( createMessage( Command.PUT.name() + " " + getRoutingTable().getLocalPeerId() + " " + myObjectPersister.toString( myInformationObject ) ));
          String theResult = ((AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID )).sendAndWaitForResponse( theMessage );
          if(!theResult.equalsIgnoreCase( Response.OK.name() )){
            LOGGER.error("Could not send information to peer '" + myPeerId + "' result '" + theResult + "'");
          }
        }
      }catch(Exception e){
        LOGGER.error("An error occured while sending message to peer '" + myPeerId + "'", e);
      }
    }
  }

  private class MyRoutingTableListener implements IRoutingTableListener {

    @Override
    public void routingTableEntryChanged( final RoutingTableEntry anEntry ) {
      //TODO it might be that this is not a new entry, but just changed, in that case we 
      //should not have send the information, nevertheless we can not know that at the moment
//      getExecutorService().execute( new SendInfoToPeer(anEntry.getPeer().getPeerId()));
      getExecutorService().execute(new GetInfoFromPeer(anEntry.getPeer().getPeerId()));
    }

    @Override
    public void routingTableEntryRemoved( RoutingTableEntry anEntry ) {
      // TODO implement this function

    }
  }
}
