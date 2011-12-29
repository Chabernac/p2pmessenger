package chabernac.protocol.packet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.tools.SystemTools;

public class TransferPanel extends JPanel{
  private static final long serialVersionUID = 1937364427569729273L;
  private static Logger LOGGER = Logger.getLogger(TransferPanel.class);
  private final AbstractTransferState myHandler;
  private JPopupMenu myPopupMenu = null;

  public TransferPanel(AbstractTransferState aHandler){
    myHandler = aHandler;
    buildGUI();
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    add(new PacketTransferProgressPanel(myHandler), BorderLayout.CENTER);
    addListeners();
  }

  private void addListeners(){
    addMouseListener(new ShowPopupMenuListener());
  }

  public Dimension getPreferredSize(){
    return new Dimension(super.getPreferredSize().width, PacketTransferProgressPanel.HEIGHT);
  }

  public class StartStopAction extends AbstractCommand{
    @Override
    public String getName() {
      switch(myHandler.getState()){
      case RUNNING : return "Stop";
      case STOPPED  : return "Start";
      case FAILED  : return "Retry";
      default : return "Stop";

      }
    }

    @Override
    public boolean isEnabled() {
      switch(myHandler.getState()){
      case RUNNING : 
      case STOPPED  :
      case FAILED  : return true;
      }
      return false;
    }

    @Override
    public void execute() {
      try{
        switch(myHandler.getState()){
        case RUNNING : myHandler.start(); break;
        case STOPPED  : myHandler.stop(); break;
        case FAILED  : myHandler.start(); break;
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
      switch(myHandler.getState()){
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

  private void buildPopupMenu(MouseEvent evt){
    if(myPopupMenu ==  null) {
      myPopupMenu = new TransferPopup(myHandler);
    }
    Point theRelativePoint = SwingUtilities.convertPoint(evt.getComponent(), evt.getX(), evt.getY(), this);
    myPopupMenu.show(this, theRelativePoint.x, theRelativePoint.y);
  }

  public class ShowPopupMenuListener extends MouseAdapter {
    public void mouseReleased(MouseEvent evt){
      if(evt.getButton() == MouseEvent.BUTTON3){
        buildPopupMenu(evt);
      }
    }
    public void mousePressed(MouseEvent evt){
      if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() >= 2){
        try {
          if(myHandler instanceof FileTransferState){
            SystemTools.openFile(((FileTransferState)myHandler).getFile());
          }
        } catch (Exception e) {
          LOGGER.error("An error occured while opening file", e);
        }
      }
    }
  }
}
