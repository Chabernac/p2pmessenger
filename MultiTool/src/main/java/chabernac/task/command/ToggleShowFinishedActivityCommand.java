package chabernac.task.command;


public class ToggleShowFinishedActivityCommand extends ActivityCommand {
  
  ToggleShowFinishedActivityCommand(){
    super();
  }

  public void executeCommand() {
    boolean visible = getTreeModel().isShowFinishedVisible(); 
    getTreeModel().setShowFinished(!visible);
    update();
  }

  public String getName() {
    boolean visible = getTreeModel().isShowFinishedVisible();
    if(visible) return "Hide completed";
    return "Show completed";
  }

  public boolean isEnabled() {
    return true;
  }
  
  public char getMnemonic(){
    if(getTreeModel().isShowFinishedVisible()){
      return 'H';
    } else {
      return 'c';
    }
  }

}
