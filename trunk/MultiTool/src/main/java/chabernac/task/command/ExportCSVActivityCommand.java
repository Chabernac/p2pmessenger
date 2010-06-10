package chabernac.task.command;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import chabernac.task.TaskTools;

public class ExportCSVActivityCommand extends ActivityCommand {
  private static Logger logger = Logger.getLogger(ExportCSVActivityCommand.class);
  
  ExportCSVActivityCommand(){
   super();
  }

  public void executeCommand() {
    TaskTools.makeCSV(getRootTask(), new File("ip.csv"));
    try{
      Runtime.getRuntime().exec("cmd /c ip.csv");
    }catch(IOException e){
      logger.error("Could not start csv", e);
    }
  }
  
  public String getName() {
    return "Export to CSV";
  }

  public boolean isEnabled() {
    return true;
  }

}
