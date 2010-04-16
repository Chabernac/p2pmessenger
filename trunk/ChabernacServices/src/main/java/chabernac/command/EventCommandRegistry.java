package chabernac.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EventCommandRegistry {
  private static EventCommandRegistry INSTANCE = null;
  
  private Set myEventCommands = null;
  
  private EventCommandRegistry(){
    myEventCommands = new HashSet();
  }
  
  private static EventCommandRegistry getInstance(){
    if(INSTANCE == null){
      INSTANCE = new EventCommandRegistry();
    }
    return INSTANCE;
  }
  
  private void registerCommand(String aCommandString, Command aCommand){
    EventCommand theEventCommand = new EventCommand(aCommandString, aCommand);
    theEventCommand.register();
    myEventCommands.add(theEventCommand);
  }
  
  private void unregisterCommand(String aCommandString){
    for(Iterator i=myEventCommands.iterator();i.hasNext();){
      EventCommand theCommand = (EventCommand)i.next();
      if(theCommand.getCommandString().equals(aCommandString)){
        theCommand.unRegister();
        i.remove();
      }
    }
  }
  
  public static void register(String aCommandString, Command aCommand){
    getInstance().registerCommand(aCommandString, aCommand);
  }
  
  public static void unRegister(String aCommandString, Command aCommand){
    getInstance().unregisterCommand(aCommandString);
  }
  
  

}
