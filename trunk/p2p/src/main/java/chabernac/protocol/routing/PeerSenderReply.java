package chabernac.protocol.routing;

import chabernac.tools.iNetworkInterface;

public class PeerSenderReply{
  //the reply from the remote peer
  private final String myReply;
  
  //the network interface through which the message was send
  private final iNetworkInterface myNetworkInterface;

  public PeerSenderReply(String aReply, iNetworkInterface aNetworkInterface) {
    super();
    myReply = aReply;
    myNetworkInterface = aNetworkInterface;
  }

  public String getReply() {
    return myReply;
  }

  public iNetworkInterface getNetworkInterface() {
    return myNetworkInterface;
  }
  
  public String toString(){
    return myReply;
  }
}

