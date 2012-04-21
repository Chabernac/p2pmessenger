package chabernac.protocol.routing;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ServerInfo;

/**
 *  This protocol will monitor the routing table and will regularry update the time distance of all peers 
 *  which are only 1 hop far.
 *  
 *  This information can then be used to calculate a fastest path to another peer instead of a path with the least hops.
 */
public class DistanceProtocol extends Protocol {
  public DistanceProtocol() {
    super("DTP");
  }

  @Override
  public String handleCommand(String aSession, String anInput) {
    return anInput;
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }
  
  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }
  
  private void start() throws ProtocolException{
   getRoutingTable().addRoutingTableListener(new RoutingTableListener()); 
  }
  
  public void setServerInfo( ServerInfo anServerInfo ) throws ProtocolException {
    super.setServerInfo(anServerInfo);
    start();
  }

  @Override
  public String getDescription() {
    return "Distance protocol";
  }
  
  public class RoutingTableListener implements IRoutingTableListener {

    @Override
    public void routingTableEntryChanged(RoutingTableEntry anEntry) {
      // TODO Auto-generated method stub
    }

    @Override
    public void routingTableEntryRemoved(RoutingTableEntry anEntry) {
      // TODO Auto-generated method stub
    }
  }

}
