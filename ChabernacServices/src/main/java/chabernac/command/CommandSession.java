/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import chabernac.util.concurrent.MonitorrableRunnableDelegate;
import chabernac.util.concurrent.iRunnableListener;
import chabernac.utils.NamedRunnable;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 15-jan-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      15-jan-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class CommandSession {
//  private static Logger LOGGER = Logger.getLogger(CommandSession.class);
  
  private static class InstanceHolder{
    private static final CommandSession INSTANCE = new CommandSession();
  }
  
  private Stack< UndoableCommand > myUndoableCommands = null;
  private Stack< UndoableCommand > myRedoableCommands = null;
  
  public static enum Mode{NORMAL, SKIP, EXCEPTION};
  
  private Mode myMode = Mode.NORMAL;
  
  private ExecutorService myExecutorService = null;
  
  private iRunnableListener myRunnableListener = null;
  
  private CommandSession(){
    init();
  }
  
  public void setNumberOfThreads(int aNumberOfThreads){
    if(myExecutorService != null){
      myExecutorService.shutdownNow();
    }
    myExecutorService = Executors.newFixedThreadPool( aNumberOfThreads );
  }
  
  
  private void init(){
    myUndoableCommands = new Stack< UndoableCommand >();
    myRedoableCommands = new Stack< UndoableCommand >();
  }
  
  public static CommandSession getInstance(){
    return InstanceHolder.INSTANCE;
  }
  
  public static synchronized CommandSession getInstance(Map aContext){
    if(!aContext.containsKey( CommandSession.class.toString() )){
      aContext.put( CommandSession.class.toString(), new CommandSession() );
    }
    return (CommandSession)aContext.get( CommandSession.class.toString() );
  }
  
  private void executeRunnable(Runnable aRunnable){
   if(myExecutorService == null) {
     aRunnable.run();
   } else {
     myExecutorService.execute( aRunnable );
   }
  }
  
  public int getActiveCount(){
    return ((ThreadPoolExecutor)myExecutorService).getActiveCount();
  }
  
  public void execute(final Command aCommand) throws CommandException{
    if(inspectMode()) {
      Runnable theRunnable =
        new NamedRunnable("CommandSession execute command '" + aCommand.getClass().getName()  + "'") {
          @Override
          protected void doRun() {
            aCommand.execute();
            if(aCommand instanceof UndoableCommand){
              myUndoableCommands.add((UndoableCommand)aCommand);
            }
            
          }
        };
      
      if(myRunnableListener != null){
        MonitorrableRunnableDelegate theMonitorrableRunnable = new MonitorrableRunnableDelegate(theRunnable);
        theMonitorrableRunnable.addListener( myRunnableListener );
        theMonitorrableRunnable.setExtraInfo( aCommand.getClass().getName() );
        theRunnable = theMonitorrableRunnable;
      }
      
      executeRunnable(theRunnable);
    }
  }
  
  private boolean inspectMode() {
    if(myMode == Mode.NORMAL) return true;
    if(myMode == Mode.SKIP) return false;
    if(myMode == Mode.EXCEPTION) throw new CommandException("Forced exception on running command");
    return true;
  }

  public void undoNumberOfSteps(int aNumber) throws CommandException{
    for(int i=aNumber;i>0 && myUndoableCommands.size() > 0;i--){
      executeRunnable( new Runnable() {
        @Override
        public void run() {
          UndoableCommand theCommand = myUndoableCommands.peek();
          if(inspectMode()) {
            theCommand.undo();
            myUndoableCommands.pop();
            myRedoableCommands.push( theCommand );    
          }
        }
      });
      
    }
  }
  
  public void undoAll() throws CommandException{
    undoNumberOfSteps( myUndoableCommands.size() );
  }
  
  public void redoNumberOfSteps(int aNumber) throws CommandException{
    for(int i=aNumber;i>0 && myRedoableCommands.size() > 0;i--){
      UndoableCommand theCommand = myRedoableCommands.peek();
      if(inspectMode()) theCommand.execute();
      myRedoableCommands.pop();
      myUndoableCommands.push( theCommand );
    }
  }
  
  public void redoAll() throws CommandException{
    redoNumberOfSteps( myRedoableCommands.size() );
  }
  
  public void reset(){
    myRedoableCommands.clear();
    myUndoableCommands.clear();
    myMode = Mode.NORMAL;
  }

  public Mode getMode() {
    return myMode;
  }

  public void setMode( Mode anMode ) {
    myMode = anMode;
  }

  public iRunnableListener getRunnableListener() {
    return myRunnableListener;
  }

  public void setRunnableListener( iRunnableListener aRunnableListener ) {
    myRunnableListener = aRunnableListener;
  }
}
