package chabernac.p2pclient.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import chabernac.p2pclient.gui.ChatMediator;

public class FocusInputAction extends AbstractAction {
  private static final long serialVersionUID = -610125396515596673L;
  private final ChatMediator myMediator;

  public FocusInputAction(ChatMediator anMediator) {
    myMediator = anMediator;
  }

  @Override
  public void actionPerformed( ActionEvent anEvent ) {
    JTextArea theMessageField = (JTextArea)myMediator.getMessageProvider(); 
    if(!theMessageField.isFocusOwner()){
      theMessageField.setText(theMessageField.getText() + anEvent.getActionCommand());
      theMessageField.requestFocus();
    }
  }

}
