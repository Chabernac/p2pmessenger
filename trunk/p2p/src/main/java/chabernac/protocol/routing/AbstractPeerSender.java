/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chabernac.protocol.routing.PeerMessage.State;

public abstract class AbstractPeerSender implements iPeerSender {
  private long myBytesSend = 0;
  private long myBytesReceived = 0;
  private long myInitTime = System.currentTimeMillis();
  private List<iPeerSenderListener> myPeerSenderListeners = new ArrayList<iPeerSenderListener>();
  private MessageListenerDelegate myListenerDelegate = new MessageListenerDelegate();

  private boolean isKeepHistory = false;
  private List<PeerMessage> myHistory = new ArrayList<PeerMessage>();

  @Override
  public final String send( AbstractPeer aPeer, String aMessage ) throws IOException {
    return send(aPeer, aMessage, 5);
  }

  @Override
  public final String send( AbstractPeer aTo, String aMessage, int aTimeout ) throws IOException {
    PeerMessage theMessage = new PeerMessage(aMessage, aTo);
    if(isKeepHistory){
      myHistory.add(theMessage);
    }
    theMessage.setListener( myListenerDelegate );
    theMessage.setState( State.INIT );

    try{
      if(aMessage.length() < 3) throw new IOException("Can not send message which has no protocol");
      String theProtocol = aMessage.substring( 0, 3 );
      if(!aTo.isProtocolSupported( theProtocol )) throw new IOException("The protocol '" + theProtocol + "' is not supported by peer '" + aTo.getPeerId() + "'");

      String theResponse = doSend(theMessage, aTimeout);

      theMessage.setResult( theResponse );

      //each char is 2 bytes char is a unicode value
      myBytesSend += (aMessage.toCharArray().length * 2);
      myBytesReceived += (theResponse.toCharArray().length * 2);
      return theResponse;
    }catch(IOException e){
      theMessage.setState( State.NOK );
      throw e;
    }
  }

  protected abstract String doSend( PeerMessage aMessage, int aTimeout ) throws IOException;

  public long getBytesReceived() {
    return myBytesReceived;
  }

  public long getBytesSend() {
    return myBytesSend;
  }

  public long getInitTime() {
    return myInitTime;
  }

  private void notifyListeners(PeerMessage aMessage){
    for(iPeerSenderListener theListener : myPeerSenderListeners){
      theListener.messageStateChanged(aMessage);
    }
  }

  public void addPeerSenderListener(iPeerSenderListener aListener){
    myPeerSenderListeners.add(aListener);
  }

  public void removePeerSenderListener(iPeerSenderListener aListener){
    myPeerSenderListeners.remove(aListener);
  }

  private class MessageListenerDelegate implements iPeerSenderListener{
    @Override
    public void messageStateChanged( PeerMessage aMessage ) {
      notifyListeners( aMessage );
    }
  }

  public boolean isKeepHistory() {
    return isKeepHistory;
  }

  public void setKeepHistory( boolean aKeepHistory ) {
    isKeepHistory = aKeepHistory;
  }

  public void clearHistory() {
    myHistory.clear();
  }

  public List<PeerMessage> getHistory() {
    return Collections.unmodifiableList( myHistory );
  }
}
