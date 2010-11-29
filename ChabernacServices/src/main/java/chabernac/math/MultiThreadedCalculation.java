/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.math;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedCalculation {
  private int myProcessors = Runtime.getRuntime().availableProcessors();
  
  private ExecutorService myService = Executors.newFixedThreadPool( myProcessors );
  private BlockingQueue<Calculation> myCalculations = new ArrayBlockingQueue<Calculation>( myProcessors );
  
  public MultiThreadedCalculation(){
    for(int i=0;i<myProcessors ;i++){
      myService.execute( new Calculator() );
    }
  }
  
  public void calculate(Calculation aCalculation){
    myCalculations.add( aCalculation );
  }
  
  private class Calculator implements Runnable{
    public void run(){
      myCalculations.poll().execute();
    }
  }
  
  public int getProcessors(){
    return myProcessors;
  }
}
