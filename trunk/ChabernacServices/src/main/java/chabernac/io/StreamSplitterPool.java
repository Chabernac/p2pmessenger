/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class StreamSplitterPool {
  private static Logger LOGGER = Logger.getLogger(StreamSplitterPool.class);
  protected final Map< String, StreamSplitter > myStreamSplitters = new HashMap< String, StreamSplitter >();
  protected final String myId;
  public final static String ID_PREFIX = "ID:";
  
  public StreamSplitterPool ( String aId ) {
    super();
    myId = aId;
  }
  
  public String add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( ID_PREFIX + myId );
    String theRemoteId = aSplitter.readLine();
    aSplitter.setId( theRemoteId );
    if(theRemoteId != null && theRemoteId.startsWith(ID_PREFIX)){
      theRemoteId = theRemoteId.substring(ID_PREFIX.length());
      if(!addStreamSplitter( theRemoteId, aSplitter )){
        LOGGER.debug("The pool already contains a splitter for id '" + theRemoteId + "', this splitter is ignored");
        return theRemoteId;
      }
    } else {
      aSplitter.handleInput(theRemoteId);
    }
    aSplitter.startSplitting();
    return theRemoteId;
  }
  
  private boolean addStreamSplitter(String anId, StreamSplitter aStreamSplitter){
    synchronized(anId){
      if(myStreamSplitters.containsKey( anId )) return false;
      myStreamSplitters.put(anId, aStreamSplitter);
      aStreamSplitter.addStreamListener( new StreamClosedListener( anId ) );
      return true;
    }
  }
  
  public String send(String aRemoteId, String aMessage) throws IOException{
    if(!myStreamSplitters.containsKey( aRemoteId )) throw new IOException("No stream splitter found for id '" + aRemoteId + "'");
    
    synchronized(aRemoteId){
      try {
        return myStreamSplitters.get(aRemoteId).send( aMessage );
      } catch ( InterruptedException e ) {
        throw new IOException("Could not send message to '" + aRemoteId + "'", e);
      }
    }
  }
  
  public void close(String aRemoteId){
    synchronized(aRemoteId){
      if(myStreamSplitters.containsKey( aRemoteId )){
        myStreamSplitters.get(aRemoteId).close();
      }
    }
  }
  
  public boolean contains(String anId){
    return myStreamSplitters.containsKey( anId );
  }
  
  public synchronized void closeAll(){
    for(String theKey : myStreamSplitters.keySet()){
      close(theKey);
    }
  }
  
  public Map<String, StreamSplitter> getStreamSplitters(){
    return Collections.unmodifiableMap( myStreamSplitters );
  }
  
  private class StreamClosedListener implements iStreamListener{
    private final String myId;

    public StreamClosedListener ( String aId ) {
      super();
      myId = aId;
    }

    @Override
    public void streamClosed() {
      synchronized(StreamSplitterPool.this){
        myStreamSplitters.remove( myId );
      }
    }
  }
}
