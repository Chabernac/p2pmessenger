package chabernac.documentationtool.command;


import chabernac.command.Command;

public class CommandFactory {
  
  public static enum Action{
    NEW_DOCUMENT
                        };

  public CommandFactory() {
    super();
  }
  
  public Command getCommand(Action anAction){
    
    return null;
  }
}
