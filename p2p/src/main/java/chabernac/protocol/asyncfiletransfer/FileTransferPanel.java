package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;

public class FileTransferPanel extends JPanel{
  private static final long serialVersionUID = 1937364427569729273L;
  private static Logger LOGGER = Logger.getLogger(FileTransferPanel.class);
  private final FileTransferHandler myHandler;

  public FileTransferPanel(FileTransferHandler aHandler){
    myHandler = aHandler;
    buildGUI();
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    add(new FileTransferProgressPanel(myHandler), BorderLayout.CENTER);
    add(new CommandButton(new StartStopAction()), BorderLayout.EAST);
  }

  public Dimension getPreferredSize(){
    return new Dimension(super.getPreferredSize().width, FileTransferProgressPanel.HEIGHT);
  }

  public class StartStopAction extends AbstractCommand{
    @Override
    public String getName() {
      if(myHandler.getState().getState() == FileTransferState.State.RUNNING){
        return "Stop";
      } else if(myHandler.getState().getState() == FileTransferState.State.PAUSED){
        return "Start";
      } else {
        return "Remove"; 
      }
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void execute() {
      try{
        if(myHandler.getState().getState() == FileTransferState.State.RUNNING){
          myHandler.pause();
        } else if(myHandler.getState().getState() == FileTransferState.State.PAUSED){
          myHandler.resume();
        } else {
          myHandler.interrupt();
        }
      }catch(Exception e){
        LOGGER.error("Could not invoke command", e);
      } 
      
      notifyObs();
    }
  }
}
