package chabernac.protocol.routing;

import java.io.IOException;

public abstract class AbstractPeer {
  private String myPeerId;
  private String myChannel;
  
  public AbstractPeer(){
  }
  
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

}