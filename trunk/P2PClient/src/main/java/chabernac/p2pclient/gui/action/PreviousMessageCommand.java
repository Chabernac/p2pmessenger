package chabernac.p2pclient.gui.action;

import chabernac.command.CommandException;
import chabernac.command.UndoableCommand;
import chabernac.p2pclient.gui.ChatMediator;

public class PreviousMessageCommand extends AbstractMediatorCommand implements UndoableCommand{

  private static final long serialVersionUID = -4348764850887706254L;

  public PreviousMessageCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restorePreviousMessage();
  }

  @Override
  public void undo() throws CommandException {
    // TODO Auto-generated method stub
    
  }

}
