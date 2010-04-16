package chabernac.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;

public class CommandMenuItem extends JMenuItem implements Observer{
  private static final long serialVersionUID = 1882923648121239843L;
  private AbstractCommand myCommand = null;
  public CommandMenuItem(AbstractCommand aCommand){
    super();
    myCommand = aCommand;
    refresh();
    addMenuItemListener(aCommand);
    aCommand.addObserver(this);
  }

  private void addMenuItemListener(final Command aCommand){
    addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        aCommand.execute();
      }
    });
  }
  
  private void refresh(){
    setEnabled(myCommand.isEnabled());
    setText(myCommand.getName());
  }

  public void update(Observable anO, Object anArg) {
    refresh();
  }

}