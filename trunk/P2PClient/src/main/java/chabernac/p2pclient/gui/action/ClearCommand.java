package chabernac.p2pclient.gui.action;

import java.util.Set;

import chabernac.command.CommandException;
import chabernac.command.UndoableCommand;
import chabernac.p2pclient.gui.ChatMediator;

public class ClearCommand extends AbstractMediatorCommand implements UndoableCommand{
  private static final long serialVersionUID = -610125396515596673L;
  private String myText = null;
  private Set<String> mySelectedUsers;

  public ClearCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myText = myMediator.getMessageProvider().getMessage();
    mySelectedUsers = myMediator.getUserSelectionProvider().getSelectedUsers();
    myMediator.clear();
  }

  @Override
  public void undo() throws CommandException {
    myMediator.getMessageProvider().setMessage( myText );
    myMediator.getUserSelectionProvider().setSelectedUsers( mySelectedUsers );    
  }

}
