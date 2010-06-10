package chabernac.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;

public class OptionPaneStatusDispatcher extends StatusDispatcher implements iEventListener{
  private JFrame myRoot = null;
  
  public OptionPaneStatusDispatcher(JFrame aRootFrame){
    myRoot = aRootFrame;
    ApplicationEventDispatcher.addListener(this, StatusEvent.class);
  }

  private void setError(String anError) {
    JOptionPane.showMessageDialog(myRoot, anError, null, JOptionPane.ERROR_MESSAGE);
  }

  private void setMessage(String aMessage) {
    JOptionPane.showMessageDialog(myRoot, aMessage, null, JOptionPane.INFORMATION_MESSAGE);

  }

  private void setWarning(String aWarning) {
    JOptionPane.showMessageDialog(myRoot, aWarning, null, JOptionPane.WARNING_MESSAGE);
  }

  public void eventFired(Event anEvent) {
    StatusEvent theEvent = (StatusEvent)anEvent;
    if(theEvent.getType() == StatusEvent.WARNING) setWarning(theEvent.getDescription());
    else if(theEvent.getType() == StatusEvent.MESSAGE) setMessage(theEvent.getDescription());
    else if(theEvent.getType() == StatusEvent.ERROR) setError(theEvent.getDescription());    
  }

}
