package chabernac.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class ClearMessageAction extends AbstractMediatorCommand {
  private static final long serialVersionUID = -610125396515596673L;

  public ClearMessageAction(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.clear();
  }

}
