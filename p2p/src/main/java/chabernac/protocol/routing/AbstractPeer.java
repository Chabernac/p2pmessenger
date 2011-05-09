package chabernac.protocol.routing;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPeer implements Serializable{
  private static final long serialVersionUID = 4466216283560470711L;
  private String myPeerId;
  private String myChannel;
  private Set<String> mySupportedProtocols = new HashSet< String >();
  private boolean isTemporaryPeer = true;
  
  public AbstractPeer(String anPeerId) {
    super();
    myPeerId = anPeerId;
  }
  
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

  public void addSupportedProtocol(String aProtocolId){
    mySupportedProtocols.add(aProtocolId);
  }
  
  public boolean isProtocolSupported(String aProtocolId){
    //if the list is empty we support all protocols for backward compatibility
    if(mySupportedProtocols == null || mySupportedProtocols.size() == 0) return true;
    return mySupportedProtocols.contains( aProtocolId );
  }
  
  public Set<String> getSupportedProtocols(){
    return Collections.unmodifiableSet( mySupportedProtocols );
  }

  public boolean isTemporaryPeer() {
    return isTemporaryPeer;
  }

  public void setTemporaryPeer( boolean aTemporaryPeer ) {
    isTemporaryPeer = aTemporaryPeer;
  }
}