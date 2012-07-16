package chabernac.protocol.routing;

/**
 * This class will inspect the routing table and replace socketpeers with indirectreacable peers
 * if the network interface for the incoming  request is different as the network interfrace
 * trough wich a peer is  reachable 
 *
 */
public class InterfaceRoutingTableInspector implements iRoutingTableInspector {
  private final SessionData mySessionData;
  
  

  public InterfaceRoutingTableInspector(SessionData aSessionData) {
    super();
    mySessionData = aSessionData;
  }



  @Override
  public RoutingTable inspectRoutingTable(String aSessionId,
      RoutingTable aRoutingTable) {
    // TODO Auto-generated method stub
    return null;
  }

}
