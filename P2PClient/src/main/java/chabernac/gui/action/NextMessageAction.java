package chabernac.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class NextMessageAction extends AbstractMediatorCommand {
  private static final long serialVersionUID = -5272770055123623782L;

  public NextMessageAction(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restoreNextMesssage();
  }

}
