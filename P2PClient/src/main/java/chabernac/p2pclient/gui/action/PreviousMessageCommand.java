package chabernac.p2pclient.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class PreviousMessageCommand extends AbstractMediatorCommand {

  private static final long serialVersionUID = -4348764850887706254L;

  public PreviousMessageCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restorePreviousMessage();
  }

}
