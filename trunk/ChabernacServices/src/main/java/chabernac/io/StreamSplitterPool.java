/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public class StreamSplitterPool {
  public final static String ID_PREFIX = "ID:";
  public final static String STATUS_PREFIX = "STATUS:";
  
  public static enum Status {OK, NOT_ADDED};
  
  private static Logger LOGGER = Logger.getLogger(StreamSplitterPool.class);

  protected final Map< String, StreamSplitter > myStreamSplitters = new HashMap< String, StreamSplitter >();
  protected final String myId;
  private final ExecutorService myExecutorService;

  public StreamSplitterPool ( String aId, ExecutorService anExecutorService ) {
    super();
    myId = aId;
    myExecutorService = anExecutorService;
  }

  public String getId(){
    return myId;
  }

  public String add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( ID_PREFIX + myId );
    String theRemoteId = aSplitter.readLine();
    
    if(!theRemoteId.startsWith( ID_PREFIX )) throw new IOException("Expected id prefix but got '" + theRemoteId + "'");
    theRemoteId = theRemoteId.substring( ID_PREFIX.length() );
    
    if(!addStreamSplitter( theRemoteId, aSplitter )){
      aSplitter.sendWithoutReply( STATUS_PREFIX + Status.NOT_ADDED.name() );
      aSplitter.close();
      if(myStreamSplitters.containsKey( theRemoteId ) || theRemoteId.equals( myId )) return theRemoteId;
      throw new IOException("The stream splitter was not accepted");
    } else {
      aSplitter.sendWithoutReply( STATUS_PREFIX + Status.OK.name() );
    }
    
    String theRemoteStatus = aSplitter.readLine();
    if(!theRemoteStatus.startsWith( STATUS_PREFIX )) throw new IOException("Expected status prefix but got '" + theRemoteStatus + "'");
    theRemoteStatus = theRemoteStatus.substring( STATUS_PREFIX.length() );
    
    Status theStatus = Status.valueOf( theRemoteStatus );
    
    if(theStatus != Status.OK) throw new IOException("The stream splitter was not accepted by the remote peer '" + theStatus + "'");
    
    aSplitter.startSplitting(myExecutorService);
    return theRemoteId;
  }

  private boolean addStreamSplitter(String anId, StreamSplitter aStreamSplitter){
    if(myId.equals( anId )) {
      LOGGER.error( "The given stream splitter has the same id '" + anId + "' as the local id '" + myId + "'" );
      return false;
    }
    
    synchronized(anId){
      if(myStreamSplitters.containsKey( anId )) {
        LOGGER.error( "The given stream splitter already exists '" + anId + "'" );
        return false;
      }
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
