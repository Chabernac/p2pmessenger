/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.task.command;

import java.util.Hashtable;

import chabernac.command.AbstractCommand;

/**
 *
 *
 * @version v1.0.0      Dec 22, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Dec 22, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class CommandFactory {
  private static Hashtable myCommands = new Hashtable();
  
  public static AbstractCommand getCommand(String aCommand){
    if(!myCommands.containsKey(aCommand)){
      if(aCommand.equalsIgnoreCase("startstop")){
        myCommands.put("startstop", new StartStopActivityCommand());
      } else if(aCommand.equalsIgnoreCase("completed")){
        myCommands.put("completed", new CompletedActivityCommand());
      } else if(aCommand.equalsIgnoreCase("create")){
        myCommands.put("create", new CreateActivityCommand());
      } else if(aCommand.equalsIgnoreCase("current")){
        myCommands.put("current", new CurrentActivityCommand());
      } else if(aCommand.equalsIgnoreCase("default")){
        myCommands.put("default", new DefaultActivityCommand());
      } else if(aCommand.equalsIgnoreCase("export")){
        myCommands.put("export", new ExportCSVActivityCommand());
      } else if(aCommand.equalsIgnoreCase("modify")){
        myCommands.put("modify", new ModifyActivityCommand());
      } else if(aCommand.equalsIgnoreCase("mostimportant")){
        myCommands.put("mostimportant", new MostImportantCommand());
      } else if(aCommand.equalsIgnoreCase("remove")){
        myCommands.put("remove", new RemoveActivityCommand());
      } else if(aCommand.equalsIgnoreCase("parent")){
        myCommands.put("parent", new SetParentActivityCommand());
      } else if(aCommand.equalsIgnoreCase("showfinished")){
        myCommands.put("showfinished", new ToggleShowFinishedActivityCommand());
      } else if(aCommand.equalsIgnoreCase("todo")){
        myCommands.put("todo", new AddToToDoCommand());
      } else if(aCommand.equalsIgnoreCase("search")){
        myCommands.put("search", new SearchCommand() );
      }
    }
    return (AbstractCommand)myCommands.get(aCommand);
  }

}
