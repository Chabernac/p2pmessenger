package chabernac.p2pclient.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class SendMessageCommand extends AbstractMediatorCommand {

  public SendMessageCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.send();
  }
}
