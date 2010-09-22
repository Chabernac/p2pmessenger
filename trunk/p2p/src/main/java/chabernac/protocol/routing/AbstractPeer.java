package chabernac.protocol.routing;

import java.io.IOException;
import java.io.Serializable;

public abstract class AbstractPeer implements Serializable{
  private static final long serialVersionUID = 4466216283560470711L;
  private String myPeerId;
  private String myChannel;
  
  public AbstractPeer(String anPeerId) {
    super();
    myPeerId = anPeerId;
  }

  public abstract String send(String aMessage) throws IOException;

  public String getPeerId() {
    return myPeerId;
  }
  
  public void setPeerId(String anPeerId) {
    myPeerId = anPeerId;
  }

  public abstract boolean isSameEndPointAs(AbstractPeer aPeer);

  public String getChannel() {
    return myChannel;
  }

  public void setChannel(String anChannel) {
    myChannel = anChannel;
  }
  
  public boolean isOnSameChannel(AbstractPeer anOtherPeer){
    return getChannel().equalsIgnoreCase(anOtherPeer.getChannel());
  }
  
  public abstract boolean isValidEndPoint();
  
  public abstract String getEndPointRepresentation();
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof SocketPeer)) return false;
    SocketPeer thePeer = (SocketPeer)anObject;

    return myPeerId.equals(thePeer.getPeerId());
  }

  public int hashCode(){
    return myPeerId.hashCode();
  }

}