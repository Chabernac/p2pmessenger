package chabernac.protocol.routing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import chabernac.protocol.IProtocol;

public class JVMPeerSender {
  private static final class INSTANCE_HOLDER{
    public static JVMPeerSender SENDER = new JVMPeerSender(); 
  }
  
  private Map<String, IProtocol> myLocalJVMProtocols = new HashMap<String, IProtocol>();
  
  private Object LOCK = new Object();
  
  public static JVMPeerSender getInstance(){
    return INSTANCE_HOLDER.SENDER;
  }
  
  public void addPeerProtocol(String aPeer, IProtocol aProtocol){
    synchronized(LOCK){
      myLocalJVMProtocols.put(aPeer, aProtocol);
    }
  }
  
  public void removePeerProtocol(String aPeer){
    synchronized (LOCK) {
      myLocalJVMProtocols.remove(aPeer);
    }
  }
  
  public boolean containsPeerProtocol(String aPeer){
    return myLocalJVMProtocols.containsKey(aPeer);
  }
  
  public String send(String aPeer, String aMessage) throws IOException{
    IProtocol theProtocol = null;
    synchronized (LOCK) {
      if(containsPeerProtocol(aPeer)) {
        theProtocol = myLocalJVMProtocols.get(aPeer);
      }
    }
    if(theProtocol == null) throw new IOException("No protocol for peer '" + aPeer + "'");
    
    return theProtocol.handleCommand(UUID.randomUUID().toString(), aMessage);
  }
}
