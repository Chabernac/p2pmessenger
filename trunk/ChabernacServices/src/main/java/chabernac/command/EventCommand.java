package chabernac.command;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.CommandEvent;
import chabernac.event.Event;
import chabernac.event.iEventListener;

public class EventCommand implements Command, iEventListener {
  private String myCommandString = null;
  private Command myCommand = null;
  
  public EventCommand(String aCommandString, Command aCommand){
    myCommandString = aCommandString;
    myCommand = aCommand;
  }
  
  public void register(){
    ApplicationEventDispatcher.addListener(this, CommandEvent.class);
  }
  
  public void unRegister(){
    ApplicationEventDispatcher.removeListener(this);
  }

  public void execute() {
    myCommand.execute();
  }

  public void eventFired(Event anEvent) {
    CommandEvent theEvent = (CommandEvent)anEvent;
    if(theEvent.getCommand() != null && theEvent.getCommand().equals(myCommandString)){
      execute();
    }
  }

  public Command getCommand() {
    return myCommand;
  }

  public void setCommand(Command anCommand) {
    myCommand = anCommand;
  }

  public String getCommandString() {
    return myCommandString;
  }

  public void setCommandString(String anCommandString) {
    myCommandString = anCommandString;
  }
  
}
