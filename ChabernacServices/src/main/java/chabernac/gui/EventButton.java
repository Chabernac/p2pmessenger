package chabernac.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.CommandEvent;

public class EventButton extends JButton {
  private static final long serialVersionUID = 2574021810940925234L;

  public EventButton(String anText, final String aCommandString) {
    super(anText);
    addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent anE) {
        ApplicationEventDispatcher.fireEvent(new CommandEvent(aCommandString));
      }  
    });
  }
}
