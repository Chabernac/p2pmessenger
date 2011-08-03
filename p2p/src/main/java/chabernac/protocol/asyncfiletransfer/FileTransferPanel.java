package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;

public class FileTransferPanel extends JPanel implements iFileTransferListener{
  private static final long serialVersionUID = 1937364427569729273L;
  private static Logger LOGGER = Logger.getLogger(FileTransferPanel.class);
  private final FileTransferHandler myHandler;
  private AbstractCommand myStartStopCommand = null;
  private AbstractCommand myCancelCommand = null;

  public FileTransferPanel(FileTransferHandler aHandler){
    myHandler = aHandler;
    buildGUI();
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    add(new FileTransferProgressPanel(myHandler), BorderLayout.CENTER);
    myStartStopCommand = new StartStopAction();
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    theButtonPanel.add(new CommandButton(myStartStopCommand, 70));
    myCancelCommand = new CancelCommand();
    theButtonPanel.add(new CommandButton(myCancelCommand, 80));
    add(theButtonPanel, BorderLayout.EAST);

    addListeners();
  }

  private void addListeners(){
    try {
      myHandler.addFileTransferListener(this);
    } catch (AsyncFileTransferException e) {
    }
  }

  public Dimension getPreferredSize(){
    return new Dimension(super.getPreferredSize().width, FileTransferProgressPanel.HEIGHT);
  }

  public class StartStopAction extends AbstractCommand{
    @Override
    public String getName() {
      switch(myHandler.getState().getState()){
      case RUNNING : return "Stop";
      case PAUSED  : return "Start";
      case FAILED  : return "Retry";
      default : return "Stop";

      }
    }

    @Override
    public boolean isEnabled() {
      switch(myHandler.getState().getState()){
      case RUNNING : 
      case PAUSED  :
      case FAILED  : return true;
      }
      return false;
    }

    @Override
    public void execute() {
      try{
        switch(myHandler.getState().getState()){
        case RUNNING : myHandler.pause(); break;
        case PAUSED  : myHandler.resume(); break;
        case FAILED  : myHandler.resume(); break;
        }
      }catch(Exception e){
        LOGGER.error("Could not invoke command", e);
      } 

      notifyObs();
    }
  }

  public class CancelCommand extends AbstractCommand{
    @Override
    public String getName() {
      switch(myHandler.getState().getState()){
      case RUNNING: return "Cancel";

      }
      return "Remove";
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void execute() {

      try{
        myHandler.cancel();
      }catch(Exception e){
        LOGGER.error("Could not invoke command", e);
      } 

      notifyObs();
    }
  }

  @Override
  public void transferStateChanged() {
    myStartStopCommand.notifyObs();
    myCancelCommand.notifyObs();
  }
}
