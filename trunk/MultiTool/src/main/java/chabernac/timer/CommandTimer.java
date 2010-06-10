package chabernac.timer;

import java.util.ArrayList;

import chabernac.command.Command;

public class CommandTimer implements Runnable{
//  private static Logger logger = Logger.getLogger(CommandTimer.class);
  
  private ArrayList myCommands = null;
  private long myTimeout;
  private boolean stop = false;
  
  public CommandTimer(long aTimeout){
    myCommands = new ArrayList();
    myTimeout = aTimeout;
  }
  
  public void addCommand(Command aCommand){
    if(!myCommands.contains(aCommand)){
      myCommands.add(aCommand);
    }
  }
  
  public void removeCommand(Command aCommand){
    myCommands.remove(aCommand);
  }
  
  public void startTimer(){
    new Thread(this).start();
  }
  
  public void run(){
    stop = false;
    long startTime, interval;
    while(!stop){
      startTime = System.currentTimeMillis();
      for(int i=0;i<myCommands.size();i++){
        try{
          
          Command theCommand = (Command)myCommands.get(i);
          long theStartTime = System.currentTimeMillis();
          //((Command)myCommands.get(i)).execute();
          theCommand.execute();
          long theTime = System.currentTimeMillis() - theStartTime; 
          if(theTime > myTimeout){
        	  System.out.println("Command " + theCommand.toString() + " took to much time: " + theTime);
          }
        }catch(Exception e){
//          logger.error("An error occred while executing command: " + ((Command)myCommands.get(i)).toString(), e);
        }
      }
      interval = System.currentTimeMillis() - startTime;
      if(interval < myTimeout){
        try {
          Thread.sleep(myTimeout - interval);
        } catch (InterruptedException e) {
//          logger.error("could not sleep", e);
        }
      }
      
    }
  }
  
  public void stop(){
    stop = true;
  }
  
  public boolean isStopped(){
	  return stop;
  }
  
}
