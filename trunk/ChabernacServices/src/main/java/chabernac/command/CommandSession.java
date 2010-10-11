/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

import java.util.Map;
import java.util.Stack;

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
  
  private static class InstanceHolder{
    private static final CommandSession INSTANCE = new CommandSession();
  }
  
  private Stack< UndoableCommand > myUndoableCommands = null;
  private Stack< UndoableCommand > myRedoableCommands = null;
  
  public static enum Mode{NORMAL, SKIP, EXCEPTION};
  
  private Mode myMode = Mode.NORMAL;
  
  private CommandSession(){
    init();
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
  
  public void execute(Command aCommand) throws CommandException{
    if(inspectMode()) aCommand.execute();
    if(aCommand instanceof UndoableCommand){
      myUndoableCommands.add((UndoableCommand)aCommand);
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
      UndoableCommand theCommand = myUndoableCommands.peek();
      if(inspectMode()) theCommand.undo();
      myUndoableCommands.pop();
      myRedoableCommands.push( theCommand );
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
}
