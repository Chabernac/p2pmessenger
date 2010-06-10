package chabernac.task.command;


public class CurrentActivityCommand extends ActivityCommand {
  
  CurrentActivityCommand(){
    super();
  }

  protected void executeCommand() {
    goToTask(getRunningTask());
  }

  public String getName() {
    return "Goto current";
  }

  public boolean isEnabled() {
    return true;
  }

}
