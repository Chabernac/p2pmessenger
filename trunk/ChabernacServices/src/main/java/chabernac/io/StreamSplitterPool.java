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
  private static Logger LOGGER = Logger.getLogger(StreamSplitterPool.class);
  public final static String ID_PREFIX = "ID:";
  public static enum Result {ADDED, ID_ALREADY_EXISTS, IS_OWN_ID };


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

  public Result add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( ID_PREFIX + myId );
    String theRemoteId = aSplitter.readLine();

    if(!theRemoteId.startsWith( ID_PREFIX )) throw new IOException("Expected id prefix but got '" + theRemoteId + "'");
    theRemoteId = theRemoteId.substring( ID_PREFIX.length() );
    aSplitter.setId( theRemoteId );

    synchronized(theRemoteId){
      Result theResult = addStreamSplitter( theRemoteId, aSplitter );
      if(theResult != Result.ADDED){
        LOGGER.debug("Stream splitter for remote id '" + theRemoteId + "' not added in server with id '" + myId + "'");
        aSplitter.close();
        return theResult;
      } 
      
      aSplitter.sendWithoutReply( "dummy" );
      aSplitter.readLine();

      aSplitter.startSplitting(myExecutorService);
      return theResult;
    }
  }

  private Result addStreamSplitter(String anId, StreamSplitter aStreamSplitter){
    if(myId.equals( anId )) {
      LOGGER.error( "The given stream splitter has the same id '" + anId + "' as the local id '" + myId + "'" );
      return Result.IS_OWN_ID;
    }

    if(myStreamSplitters.containsKey( anId )) {
      LOGGER.error( "The given stream splitter already exists '" + anId + "'" );
      return Result.ID_ALREADY_EXISTS;
    }
    myStreamSplitters.put(anId, aStreamSplitter);
    aStreamSplitter.addStreamListener( new StreamClosedListener( anId ) );
    return Result.ADDED;
  }

  public String send(String aRemoteId, String aMessage) throws IOException{
    if(!myStreamSplitters.containsKey( aRemoteId )) throw new IOException("No stream splitter found for id '" + aRemoteId + "'");

    synchronized(aRemoteId){
      LOGGER.debug("Trying to send message from '" + myId + "' to '" + aRemoteId + "': " + aMessage);
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
        LOGGER.debug("Closing stream splitter with id '" + aRemoteId + "'");
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
        LOGGER.debug("Removing stream splitter for peer with id '" + myId + "'");
        myStreamSplitters.remove( myId );
      }
    }
  }
}
