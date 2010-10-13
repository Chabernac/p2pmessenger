package chabernac.gui.event;

import javax.swing.JFrame;

import chabernac.events.Event;

public class FocusGainedEvent extends Event{
  private static final long serialVersionUID = 3417305752171265377L;
  private final JFrame myFrame;
  
  public FocusGainedEvent(JFrame aFrame) {
    myFrame = aFrame;
  }

  public JFrame getFrame() {
    return myFrame;
  }
}
