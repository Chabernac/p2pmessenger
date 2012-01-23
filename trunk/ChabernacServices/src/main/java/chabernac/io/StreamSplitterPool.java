/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StreamSplitterPool {
  protected final Map< String, StreamSplitter > myStreamSplitters = new HashMap< String, StreamSplitter >();
  protected final String myId;
  
  public StreamSplitterPool ( String aId ) {
    super();
    myId = aId;
  }
  
  public synchronized String add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( myId );
    String theRemoteId = aSplitter.readLine();
    myStreamSplitters.put(theRemoteId, aSplitter);
    aSplitter.addStreamListener( new StreamClosedListener( theRemoteId ) );
    aSplitter.startSplitting();
    return theRemoteId;
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
  
  public void closeAll(){
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
      synchronized(myId){
        myStreamSplitters.remove( myId );
      }
    }
  }
}
