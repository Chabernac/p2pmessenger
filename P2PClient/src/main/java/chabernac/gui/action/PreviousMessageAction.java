package chabernac.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class PreviousMessageAction extends AbstractMediatorCommand {

  private static final long serialVersionUID = -4348764850887706254L;

  public PreviousMessageAction(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restorePreviousMessage();
  }

}
