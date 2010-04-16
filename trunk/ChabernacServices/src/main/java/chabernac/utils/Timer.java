/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import chabernac.command.Command;

/**
 * @version  v1.0.0      3-sep-2004  
 * 
 *  <pre>
 *    <u><i>Version History</u></i>  
 *    
 *    v1.0.0 3-sep-2004 - initial release       - Guy Chauliac
 *      
 *  </pre>
 *  
 * @author  <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class Timer implements Runnable{
  private Command myCommand = null;
  private int myDelay;
  private boolean stop = false;
  private boolean interrupted = false;

  public Timer(int aDelay, Command aCommand){
    myDelay = aDelay;
    myCommand = aCommand;
  }

  public void startTimer(){
    stop = false;
    new Thread(this).start();
  }

  public synchronized void stopTimer(){
    stop = true;
    interrupted = true;
    notifyAll();
  }

  public synchronized void run(){
    while(!stop){
      try{
        interrupted = false;
        wait(myDelay);
        if(!interrupted) myCommand.execute();
      }catch(InterruptedException e){}
    }
  }

  public synchronized void restart(){
    interrupted = true;
    notifyAll();
  }

  public static void main(String args[]){
    Command theCommand = new Command(){
      public void execute(){
        System.out.println("hallo");
      }
    };
    Timer theTimer = new Timer(1000, theCommand);
    theTimer.startTimer();
    try{
      Thread.sleep(1900);
    }catch(Exception e){}
    theTimer.restart();
    try{
      Thread.sleep(5000);
    }catch(Exception e){}
    theTimer.stopTimer();
  }



}
