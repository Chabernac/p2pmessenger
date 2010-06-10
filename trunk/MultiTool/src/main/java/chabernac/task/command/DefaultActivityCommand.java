package chabernac.task.command;


public class DefaultActivityCommand extends ActivityCommand {
  
  DefaultActivityCommand(){
    super();
  }

  public void executeCommand() {
  }

  public String getName() {
    return "default";
  }

  public boolean isEnabled() {
    return true;
  }
}
