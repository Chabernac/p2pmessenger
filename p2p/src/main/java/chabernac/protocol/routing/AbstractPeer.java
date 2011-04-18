package chabernac.protocol.routing;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import chabernac.p2p.settings.P2PSettings;

public abstract class AbstractPeer implements Serializable{
  private static final long serialVersionUID = 4466216283560470711L;
  private String myPeerId;
  private String myChannel;
  protected transient iPeerSender myPeerSender = null;
  private Set<String> mySupportedProtocols = new HashSet< String >();
  private boolean isTemporaryPeer = true;
  
  public AbstractPeer(String anPeerId) {
    super();
    myPeerId = anPeerId;
  }
  
  public final String send(String aMessage) throws IOException{
    return send(aMessage, 5);
  }

  public final String send(String aMessage, int aTimeoutInSeconds) throws IOException{
    if(aMessage.length() < 3) throw new IOException("Can not send message which has no protocol");
    String theProtocol = aMessage.substring( 0, 3 );
    if(!isProtocolSupported( theProtocol )) throw new IOException("The protocol '" + theProtocol + "' is not supported by peer '" + myPeerId + "'");
    if(myPeerSender == null) myPeerSender = P2PSettings.getInstance().getPeerSender();
    return sendMessage(aMessage, aTimeoutInSeconds);
  }
  
  protected abstract String sendMessage(String aMessage, int aTimeoutInSeconds) throws IOException;

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

  public iPeerSender getPeerSender() {
    return myPeerSender;
  }

  public void setPeerSender(iPeerSender anPeerSender) {
    myPeerSender = anPeerSender;
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