/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DynamicSizeExecutor implements ExecutorService{
//  private static Logger LOGGER = Logger.getLogger( DynamicSizeExecutor.class );
  private final ThreadPoolExecutor myExecutor;

  private static DynamicSizeExecutor TINY = null;
  private static DynamicSizeExecutor SMALL = null;
  private static DynamicSizeExecutor MEDIUM = null;
  private static DynamicSizeExecutor LARGE = null;
  private static DynamicSizeExecutor CUSTOM = null;


  public DynamicSizeExecutor(int aCoreSize, int aMaxPoolSize, int aQueueSize){
    myExecutor = new ThreadPoolExecutor( aCoreSize, aMaxPoolSize, 10, TimeUnit.SECONDS, new OfferBlockingQueue<Runnable>(aQueueSize) );
  }
  
  public DynamicSizeExecutor(int aCoreSize, int aMaxPoolSize){
    myExecutor = new ThreadPoolExecutor( aCoreSize, aMaxPoolSize, 10, TimeUnit.SECONDS, new OfferBlockingQueue<Runnable>(1024) );
  }

  public void execute(Runnable aRunnable){
    myExecutor.execute( aRunnable );
  }

  @Override
  public boolean awaitTermination( long aTimeout, TimeUnit aUnit ) throws InterruptedException {
    return myExecutor.awaitTermination( aTimeout, aUnit );
  }

  @Override
  public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> aTasks ) throws InterruptedException {
    return myExecutor.invokeAll( aTasks );
  }

  @Override
  public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> aTasks, long aTimeout, TimeUnit aUnit )
  throws InterruptedException {
    return myExecutor.invokeAll( aTasks, aTimeout, aUnit );
  }

  @Override
  public <T> T invokeAny( Collection<? extends Callable<T>> aTasks ) throws InterruptedException, ExecutionException {
    return myExecutor.invokeAny( aTasks );
  }

  @Override
  public <T> T invokeAny( Collection<? extends Callable<T>> aTasks, long aTimeout, TimeUnit aUnit ) throws InterruptedException,
  ExecutionException, TimeoutException {
    return myExecutor.invokeAny( aTasks, aTimeout, aUnit );
  }

  @Override
  public boolean isShutdown() {
    return myExecutor.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return myExecutor.isTerminated();
  }

  @Override
  public void shutdown() {
    myExecutor.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return myExecutor.shutdownNow();
  }

  @Override
  public <T> Future<T> submit( Callable<T> aTask ) {
    return myExecutor.submit( aTask );
  }

  @Override
  public Future<?> submit( Runnable aTask ) {
    return myExecutor.submit( aTask );
  }

  @Override
  public <T> Future<T> submit( Runnable aTask, T aResult ) {
    return myExecutor.submit( aTask, aResult );
  }

  public static DynamicSizeExecutor getTinyInstance(){
    return new DynamicSizeExecutor( 0, 5 );
  }

  public static DynamicSizeExecutor getSmallInstance(){
    return new DynamicSizeExecutor( 1, 20 );
  }

  public static DynamicSizeExecutor getMediumInstance(){
    return new DynamicSizeExecutor( 10, 256 );
  }

  public static DynamicSizeExecutor getLargeInstance(){
    return new DynamicSizeExecutor( 20, 1024 );
  }

  public static synchronized DynamicSizeExecutor getCachedTinyInstance(){
    if(TINY == null){
      TINY = getTinyInstance();
    }
    return TINY;
  }

  public static synchronized DynamicSizeExecutor getCachedSmallInstance(){
    if(SMALL == null){
      SMALL = getSmallInstance();
    }
    return SMALL;
  }

  public static synchronized DynamicSizeExecutor getCachedMediumInstance(){
    if(MEDIUM == null){
      MEDIUM = getMediumInstance();
    }
    return MEDIUM;
  }

  public static synchronized DynamicSizeExecutor getCachedLargeInstance(){
    if(LARGE == null){
      LARGE = getLargeInstance();
    }
    return LARGE;
  }

  public static synchronized DynamicSizeExecutor getCustomInstance(){
    if(CUSTOM == null) throw new NullPointerException( "The custom size executor was not instantiated, init it with initCustom()");
    return CUSTOM;
  }

  public static synchronized void initCustom(int aCoreSize, int aMaxPoolSize){
    if(CUSTOM == null){
      CUSTOM = new DynamicSizeExecutor( aCoreSize, aMaxPoolSize );
    }
  }

  public static synchronized void clearCachedInstances(){
    shutDown( TINY );
    TINY = null;
    shutDown( SMALL );
    SMALL = null;
    shutDown( MEDIUM );
    MEDIUM = null;
    shutDown( LARGE );
    LARGE = null;
    shutDown( CUSTOM );
    CUSTOM = null;
  }

  private static void shutDown(DynamicSizeExecutor anExecutor){
    if(anExecutor != null){
      anExecutor.shutdownNow();
    }
  }
  
  private class OfferBlockingQueue<T> extends ArrayBlockingQueue<T>{

    public OfferBlockingQueue(int aQueueSize) {
      super(aQueueSize);
    }
    
    public boolean offer(T anObject){
      try {
        put(anObject);
        return true;
      } catch (InterruptedException e) {
        return false;
      }
    }
  }
}
