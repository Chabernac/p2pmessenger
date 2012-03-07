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
  
  public static enum Result {ADDED, ID_ALREADY_EXISTS, IS_OWN_ID };
  
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

  public Result add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( ID_PREFIX + myId );
    String theRemoteId = aSplitter.readLine();
    
    if(!theRemoteId.startsWith( ID_PREFIX )) throw new IOException("Expected id prefix but got '" + theRemoteId + "'");
    theRemoteId = theRemoteId.substring( ID_PREFIX.length() );
    aSplitter.setId( theRemoteId );
    
    Result theResult = addStreamSplitter( theRemoteId, aSplitter );
    if(theResult != Result.ADDED){
      System.out.println("Stream splitter for remote id '" + theRemoteId + "' not added in server with id '" + myId + "'");
      aSplitter.sendWithoutReply( STATUS_PREFIX + theResult.name() );
      aSplitter.close();
      return theResult;
    } else {
      aSplitter.sendWithoutReply( STATUS_PREFIX + theResult.name() );
    }
    
    String theRemoteStatus = aSplitter.readLine();
    if(!theRemoteStatus.startsWith( STATUS_PREFIX )) throw new IOException("Expected status prefix but got '" + theRemoteStatus + "'");
    theRemoteStatus = theRemoteStatus.substring( STATUS_PREFIX.length() );
    
    Result theStatus = Result.valueOf( theRemoteStatus );
    
    if(theStatus != Result.ADDED) throw new IOException("The stream splitter was not accepted by the remote peer '" + theStatus + "'");
    
    System.out.println("Start splitting in server '" + myId + "' for remote server id '" + theRemoteId + "'");
    aSplitter.startSplitting(myExecutorService);
    return theResult;
  }

  private Result addStreamSplitter(String anId, StreamSplitter aStreamSplitter){
    if(myId.equals( anId )) {
      LOGGER.error( "The given stream splitter has the same id '" + anId + "' as the local id '" + myId + "'" );
      return Result.IS_OWN_ID;
    }
    
    synchronized(anId){
      if(myStreamSplitters.containsKey( anId )) {
        LOGGER.error( "The given stream splitter already exists '" + anId + "'" );
        return Result.ID_ALREADY_EXISTS;
      }
      myStreamSplitters.put(anId, aStreamSplitter);
      aStreamSplitter.addStreamListener( new StreamClosedListener( anId ) );
      return Result.ADDED;
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
