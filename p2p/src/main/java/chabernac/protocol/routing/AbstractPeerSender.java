/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.protocol.routing.PeerMessage.State;
import chabernac.utils.LimitedListDecorator;

public abstract class AbstractPeerSender implements iPeerSender {
  private static Logger LOGGER = Logger.getLogger( AbstractPeerSender.class );
  private long myBytesSend = 0;
  private long myBytesReceived = 0;
  private long myInitTime = System.currentTimeMillis();
  private List<iPeerSenderListener> myPeerSenderListeners = new ArrayList<iPeerSenderListener>();
  private MessageListenerDelegate myListenerDelegate = new MessageListenerDelegate();

  private boolean isKeepHistory = false;
  private List<PeerMessage> myHistory = new LimitedListDecorator<PeerMessage>(100,new ArrayList<PeerMessage>());

  @Override
  public final PeerSenderReply send( AbstractPeer aPeer, String aMessage ) throws IOException {
    return send(aPeer, aMessage, 5);
  }

  @Override
  public final PeerSenderReply send( AbstractPeer aTo, String aMessage, int aTimeout ) throws IOException {
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

      PeerSenderReply theResponse = doSend(theMessage, aTimeout);

      theMessage.setResult( theResponse.getReply() );

      //each char is 2 bytes char is a unicode value
      if(aMessage != null) myBytesSend += (aMessage.toCharArray().length * 2);
      if(theResponse != null) myBytesReceived += (theResponse.getReply().toCharArray().length * 2);
      return theResponse;
    }catch(IOException e){
      if(e.getCause() != null && e.getCause() instanceof ConnectException){
        //do not log the entire stacktrace when we can just not connect to this host.
        LOGGER.debug("Can not connect to peer at '" + aTo.getEndPointRepresentation() + "'");
      } else {
        LOGGER.error("Error occured while sending message", e);
      }
      theMessage.setState( State.NOK );
      throw e;
    }
  }

  protected abstract PeerSenderReply doSend( PeerMessage aMessage, int aTimeout ) throws IOException;

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
