/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

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
  private final ThreadPoolExecutor myExecutor;
  private ArrayBlockingQueue<Runnable> myQueue = new ArrayBlockingQueue<Runnable>( 64 );
  
  public DynamicSizeExecutor(int aCoreSize, int aMaxPoolSize){
    myExecutor = new ThreadPoolExecutor( aCoreSize, aMaxPoolSize, 10, TimeUnit.SECONDS, myQueue);
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
}
