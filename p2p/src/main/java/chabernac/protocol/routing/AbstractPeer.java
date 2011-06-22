package chabernac.protocol.routing;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import chabernac.tools.TestTools;

public abstract class AbstractPeer implements Serializable{
  private static final long serialVersionUID = 4466216283560470711L;
  private String myPeerId;
  private String myChannel;
  protected Set<String> mySupportedProtocols = new HashSet< String >();
  private boolean isTemporaryPeer = true;
  
  //Maybe this is not so clean but how else to define if a peer was created during a unit test?
  private final boolean isTestPeer;
  
  public AbstractPeer(String anPeerId) {
    super();
    isTestPeer = TestTools.isInUnitTest();
    myPeerId = anPeerId;
  }
  
  public String getPeerId() {
    return myPeerId;
  }
  
  public AbstractPeer setPeerId(String anPeerId) {
    myPeerId = anPeerId;
    return this;
  }

  public abstract boolean isSameEndPointAs(AbstractPeer aPeer);

  public String getChannel() {
    return myChannel;
  }

  public AbstractPeer setChannel(String anChannel) {
    myChannel = anChannel;
    return this;
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

  public AbstractPeer addSupportedProtocol(String aProtocolId){
    mySupportedProtocols.add(aProtocolId);
    return this;
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

  public AbstractPeer setTemporaryPeer( boolean aTemporaryPeer ) {
    isTemporaryPeer = aTemporaryPeer;
    return this;
  }

  public boolean isTestPeer() {
    return isTestPeer;
  }
}