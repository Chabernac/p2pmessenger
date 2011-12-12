/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutorMonitorDecorator implements ExecutorService {
  private final ExecutorService myExecutorService;
  private AtomicLong myScheduledCounter = new AtomicLong(0);
  private AtomicLong myRunningCounter = new AtomicLong(0);

  public ExecutorMonitorDecorator ( ExecutorService aExecutorService ) {
    super();
    myExecutorService = aExecutorService;
  }

  public boolean awaitTermination( long aTimeout, TimeUnit aUnit ) throws InterruptedException {
    return myExecutorService.awaitTermination( aTimeout, aUnit );
  }

  public void execute( Runnable aCommand ) {
    myScheduledCounter.incrementAndGet();
    myExecutorService.execute( new RunnableDecorator( aCommand ) );
  }

  public <T> List< Future< T >> invokeAll( Collection< ? extends Callable< T >> aTasks, long aTimeout, TimeUnit aUnit )
  throws InterruptedException {
    throw new InterruptedException("this operation is not suppored");
  }

  public <T> List< Future< T >> invokeAll( Collection< ? extends Callable< T >> aTasks ) throws InterruptedException {
    List<Future< T >> theFutures = new ArrayList< Future<T> >();
    for(Callable<T> theCallable : aTasks){
      myScheduledCounter.incrementAndGet();
      theFutures.add(myExecutorService.submit( new CallableDecorator< T >( theCallable ) ));
    }
    return theFutures;
  }

  public <T> T invokeAny( Collection< ? extends Callable< T >> aTasks, long aTimeout, TimeUnit aUnit ) throws InterruptedException{
    throw new InterruptedException("this operation is not suppored");
  }

  public <T> T invokeAny( Collection< ? extends Callable< T >> aTasks ) throws InterruptedException, ExecutionException {
    throw new InterruptedException("this operation is not suppored");
  }

  public boolean isShutdown() {
    return myExecutorService.isShutdown();
  }

  public boolean isTerminated() {
    return myExecutorService.isTerminated();
  }

  public void shutdown() {
    myExecutorService.shutdown();
  }

  public List< Runnable > shutdownNow() {
    return myExecutorService.shutdownNow();
  }

  public <T> Future< T > submit( Callable< T > aTask ) {
    myScheduledCounter.incrementAndGet();
    return myExecutorService.submit( new CallableDecorator< T >( aTask ) );
  }

  public <T> Future< T > submit( Runnable aTask, T aResult ) {
    myScheduledCounter.incrementAndGet();
    return myExecutorService.submit( new RunnableDecorator( aTask ), aResult );
  }

  public Future< ? > submit( Runnable aTask ) {
    myScheduledCounter.incrementAndGet();
    return myExecutorService.submit( new RunnableDecorator( aTask ) );
  }

  private class RunnableDecorator implements Runnable{
    private final Runnable myRunnable;

    public RunnableDecorator ( Runnable aRunnable ) {
      super();
      myRunnable = aRunnable;
    }

    public void run() {
      myScheduledCounter.decrementAndGet();
      myRunningCounter.incrementAndGet();
      try{
        myRunnable.run();
      }finally{
        myRunningCounter.decrementAndGet();
      }
    }
  }

  private class CallableDecorator<V> implements Callable< V >{
    private final Callable< V > myCallable;

    public CallableDecorator ( Callable< V > aCallable ) {
      super();
      myCallable = aCallable;
    }

    public V call() throws Exception {
      myScheduledCounter.decrementAndGet();
      myRunningCounter.incrementAndGet();
      try{
        return myCallable.call();
      }finally{
        myRunningCounter.decrementAndGet();
      }
    }
  }
}
