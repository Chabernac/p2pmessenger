package chabernac.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.CommandEvent;

public class EventMenuItem extends JMenuItem {
  private static final long serialVersionUID = 7964148905088012063L;

  public EventMenuItem(String aTitle, final String aCommandString){
    super(aTitle);
    addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent anE) {
        ApplicationEventDispatcher.fireEvent(new CommandEvent(aCommandString));
      }  
    });
  }
}
