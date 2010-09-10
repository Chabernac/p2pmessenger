/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import java.io.IOException;
import java.util.Map;

import chabernac.comet.DataHandlingException;
import chabernac.comet.EndPoint;
import chabernac.comet.iDataHandler;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class P2PDataHandler implements iDataHandler, iP2PEventHandler, IRoutingTableListener{
  private iObjectStringConverter<P2PEvent> myConverter = new Base64ObjectStringConverter<P2PEvent>();
  
  private final RoutingTable myRoutingTable;
  private Map< String, EndPoint > myEndPoints; 
  
  public P2PDataHandler(RoutingTable aRoutingTable){
    myRoutingTable= aRoutingTable;
    myRoutingTable.addRoutingTableListener( this );
  }
  

  @Override
  public void handleData( String aData, Map< String, EndPoint > anEndPoints ) throws DataHandlingException{
    //TODO this is not the correct way to get the endpoints
    myEndPoints = anEndPoints;
    
    try{
      P2PEvent theMessage = myConverter.getObject( aData );
      theMessage.handle( this );
    }catch(IOException e){
      throw new DataHandlingException("Could not handle data", e);
    }

  }


  @Override
  public void handleEvent( RoutingTableEvent anRoutingTableMessage ) {
    myRoutingTable.merge( anRoutingTableMessage.getRoutingTable() );
  }


  @Override
  public void handleEvent( MessageEvent anMessageEvent ) {
    Message theMessage = anMessageEvent.getMessage();
    Peer theGateWay = myRoutingTable.getGatewayForPeer( theMessage.getDestination() );
    EndPoint theEndPoint = myEndPoints.get( theGateWay.getPeerId() );
    theEndPoint.setData( myConverter.toString( anMessageEvent ) );
    
  }


  @Override
  public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void routingTableEntryRemoved( RoutingTableEntry anEntry ) {
    // TODO Auto-generated method stub
    
  }

}
