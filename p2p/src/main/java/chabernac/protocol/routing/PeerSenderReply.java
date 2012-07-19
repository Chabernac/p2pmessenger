package chabernac.protocol.routing;

import chabernac.io.iCommunicationInterface;

public class PeerSenderReply{
  //the reply from the remote peer
  private final String myReply;
  
  //the network interface through which the message was send
  private final iCommunicationInterface myNetworkInterface;

  public PeerSenderReply(String aReply, iCommunicationInterface aNetworkInterface) {
    super();
    myReply = aReply;
    myNetworkInterface = aNetworkInterface;
  }

  public String getReply() {
    return myReply;
  }

  public iCommunicationInterface getNetworkInterface() {
    return myNetworkInterface;
  }
  
  public String toString(){
    return myReply;
  }
}

