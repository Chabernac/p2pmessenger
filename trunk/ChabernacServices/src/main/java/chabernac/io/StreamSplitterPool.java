/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class StreamSplitterPool {
  private static Logger LOGGER = Logger.getLogger(StreamSplitterPool.class);
  public final static String ID_PREFIX = "ID:";
  public static enum Result {ADDED, CLOSED, IS_OWN_ID, CONCURRENT_CONNECTION_ATTEMPT };

  protected final Map< String, StreamSplitter > myStreamSplitters = new HashMap< String, StreamSplitter >();
  protected final String myId;
  private final ExecutorService myExecutorService;
  private final Map<String, AtomicInteger> mySimultanousConnectionAttempts = new HashMap<String, AtomicInteger>();
  
  private final List<iStreamSplitterPoolListener> myStreamSplitterPoolListeners = new ArrayList<iStreamSplitterPoolListener>();
  
  private ExecutorService myListenerService = Executors.newSingleThreadExecutor();

  public StreamSplitterPool ( String aId, ExecutorService anExecutorService ) {
    super();
    myId = aId;
    myExecutorService = anExecutorService;
  }

  public String getId(){
    return myId;
  }
  
  public void addStreamSplitterPoolListener(iStreamSplitterPoolListener aListener){
    myStreamSplitterPoolListeners.add(aListener);
  }
  
  public void removeStreamSplitterPoolListener(iStreamSplitterPoolListener aListener){
    myStreamSplitterPoolListeners.remove(aListener);
  }
  
  private void notifyStreamSplitterAddedRemoved(final StreamSplitter aStreamSplitter, final boolean isAdded){
    System.out.println("Stream splitter added/removed " + isAdded + " " + aStreamSplitter.getId());
    myListenerService.execute(new Runnable(){
      public void run(){
        for(iStreamSplitterPoolListener theListener : myStreamSplitterPoolListeners){
          if(isAdded){
            theListener.streamSplitterAdded(aStreamSplitter);
          } else {
            theListener.streamSplitterRemoved(aStreamSplitter);
          }
        }
      }
    });
  }
  

  private int incrementConnectionAttempts(String anId){
    synchronized(anId.intern()){
      if(!mySimultanousConnectionAttempts.containsKey( anId )){
        mySimultanousConnectionAttempts.put( anId, new AtomicInteger(0) ); 
      }
      return mySimultanousConnectionAttempts.get(anId).incrementAndGet();
    }
  }

  private void decrementConnectionAttempts(String anId){
    synchronized ( anId.intern() ) {
      if(mySimultanousConnectionAttempts.containsKey( anId )){
        int theCount = mySimultanousConnectionAttempts.get( anId ).decrementAndGet();
        if(theCount <= 0){
          mySimultanousConnectionAttempts.remove( anId );
        }
      }
    }
  }

  public Result add(StreamSplitter aSplitter) throws IOException{
    if(myStreamSplitters.values().contains( aSplitter )) throw new IOException("The pool alredy contains this streamsplitter");
    //write our own id
    aSplitter.sendWithoutReply( ID_PREFIX + myId );
    String theRemoteId = aSplitter.readLine();

    if(!theRemoteId.startsWith( ID_PREFIX )) throw new IOException("Expected id prefix but got '" + theRemoteId + "'");
    theRemoteId = theRemoteId.substring( ID_PREFIX.length() );
    aSplitter.setId( theRemoteId );

    try{
      if(incrementConnectionAttempts( theRemoteId ) > 1){
        aSplitter.sendWithoutReply( Result.CONCURRENT_CONNECTION_ATTEMPT.name() );
        aSplitter.close();
        return Result.CONCURRENT_CONNECTION_ATTEMPT;
      }
      aSplitter.sendWithoutReply( "OK" );

      try{
        if(aSplitter.readLine().equals( Result.CONCURRENT_CONNECTION_ATTEMPT.name() )){
          aSplitter.close();
          return Result.CONCURRENT_CONNECTION_ATTEMPT;
        }
      }catch(Exception e){
        return Result.CONCURRENT_CONNECTION_ATTEMPT;
      }

      Result theResult = addStreamSplitter( theRemoteId, aSplitter );
      if(theResult != Result.ADDED){
        return theResult;
      } 

      aSplitter.sendWithoutReply( "dummy" );
      aSplitter.readLine();

      aSplitter.startSplitting(myExecutorService);
      return theResult;
    }catch(IOException e){
      LOGGER.error("An error occured while setting up stream splitter for remote id '" + theRemoteId + "' closing it ", e);
      aSplitter.close();
      return Result.CLOSED;
    } finally {
      decrementConnectionAttempts( theRemoteId );
    }
  }

  private Result addStreamSplitter(String aRemoteId, StreamSplitter aStreamSplitter){
    if(myId.equals( aRemoteId )) {
      LOGGER.error( "The given stream splitter has the same id '" + aRemoteId + "' as the local id '" + myId + "'" );
      aStreamSplitter.close();
      return Result.IS_OWN_ID;
    }

    if(myStreamSplitters.containsKey( aRemoteId )) {
      aStreamSplitter.close();
      LOGGER.error( "We have detected a stream splitter with the remote id'" + aRemoteId + "' already exists, closing the new stream splitter" );
      return Result.CLOSED;
    }

    myStreamSplitters.put(aRemoteId, aStreamSplitter);
    notifyStreamSplitterAddedRemoved(aStreamSplitter, true);
    aStreamSplitter.addStreamListener( new StreamClosedListener( aRemoteId, aStreamSplitter ) );
    return Result.ADDED;
  }

  public String send(String aRemoteId, String aMessage) throws IOException{
    synchronized(aRemoteId.intern()){
      if(!myStreamSplitters.containsKey( aRemoteId )) throw new IOException("No stream splitter found for id '" + aRemoteId + "'");
      //      LOGGER.debug("Trying to send message from '" + myId + "' to '" + aRemoteId + "': " + aMessage);
      try {
        return myStreamSplitters.get(aRemoteId).send( aMessage );
      } catch ( InterruptedException e ) {
        throw new IOException("Could not send message to '" + aRemoteId + "'", e);
      }
    }
  }

  public void close(String aRemoteId){
    synchronized(aRemoteId.intern()){
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
    for(String theKey : new HashSet<String>(myStreamSplitters.keySet())){
      close(theKey);
    }
  }

  public Map<String, StreamSplitter> getStreamSplitters(){
    return Collections.unmodifiableMap( myStreamSplitters );
  }

  private class StreamClosedListener extends StreamSplitterListenerAdapter{
    private final String myId;
    private final StreamSplitter myStreamSplitter;

    public StreamClosedListener ( String aId, StreamSplitter aStreamSplitter ) {
      super();
      myId = aId;
      myStreamSplitter = aStreamSplitter;
    }

    @Override
    public void streamClosed() {
      synchronized(myId.intern()){
//        LOGGER.debug("Removing stream splitter for peer with id '" + myId + "'");
        if(myStreamSplitters.containsKey( myId ) && myStreamSplitters.get( myId ) == myStreamSplitter){
          StreamSplitter theRemovedSplitter = myStreamSplitters.remove( myId );
          notifyStreamSplitterAddedRemoved(theRemovedSplitter, false);
        }
      }
    }
  }
}
