/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandExecutor {
  private static CommandExecutor INSTANCE = new CommandExecutor();
  
  private ExecutorService myService = Executors.newFixedThreadPool( 1 );
  
  public static CommandExecutor getInstance(){
    return INSTANCE;
  }
  
  public <T> Future<T> executeCallable(Callable< T > aCallable){
    return myService.submit( aCallable );
  }
}
