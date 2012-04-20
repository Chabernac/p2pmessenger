package chabernac.protocol.packet;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.tools.SystemTools;

public class TransferPopup extends JPopupMenu {
  private static final long serialVersionUID = -1777097383738597120L;
  private static final Logger LOGGER = Logger.getLogger(TransferPopup.class);
  private final AbstractTransferState myHandler;
  private FileTransferState.State myLastState = null;
  private PacketVisualizerFrame myPacketVisualizer = null;

  public TransferPopup(AbstractTransferState aHandler){
    myHandler = aHandler;
    buildMenu();
    addListeners();
  }

  private void addListeners() {
    myHandler.addStateChangeListener( new StateChangeListener() );
  }

  private synchronized void buildMenu(){
    State theState = myHandler.getState();
    if(myLastState != theState){
      removeAll();
      switch(theState){
      case DONE:{
        add(new OpenAction());
        add(new ExploreAction());
        add(new RemoveAction());
        break;
      }
      case PENDING:{
        add(new RemoveAction());
        break;
      }
      case FAILED:
      case STOPPED:{
        add(new StartAction("Download herstarten"));
        add(new RemoveAction());
        break;
      }
      case RUNNING:{
        add(new StopAction());
        add(new RemoveAction());
        add(new VisualizePacketAction());
        break;
      }
      }
    }
    myLastState = theState;
  }

  private class StateChangeListener implements iStateChangeListener {
    @Override
    public void stateChanged( String aTransferId, State anOldState, State aNewState ) {
      buildMenu();
    }
  }

  private class OpenAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public OpenAction(){
      putValue( Action.NAME, "Openen" );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      try {
        if(myHandler instanceof FileTransferState){
          SystemTools.openFile(((FileTransferState)myHandler).getFile());
        }
      } catch (Exception e) {
        LOGGER.error("An error occured while opening file", e);
      }
    }
  }

  private class ExploreAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public ExploreAction(){
      putValue( Action.NAME, "Folder openen" );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      try {
        if(myHandler instanceof FileTransferState){
          SystemTools.openDirectory(((FileTransferState)myHandler).getFile());
        }
      } catch (Exception e) {
        LOGGER.error("An error occured while opening directory", e);
      }
    }
  }

  private class RemoveAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public RemoveAction(){
      putValue( Action.NAME, "Verwijderen" );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      try {
        myHandler.cancel();
      } catch (Exception e) {
        LOGGER.error("An error occured while cancelling download", e);
      }
    }
  }

  private class StartAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public StartAction(String aName){
      putValue( Action.NAME, aName );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      try {
        myHandler.start();
      } catch (Exception e) {
        LOGGER.error("An error occured while resuming download", e);
      }
    }
  }

  private class StopAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public StopAction(){
      putValue( Action.NAME, "Download pauzeren" );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      try {
        myHandler.stop();
      } catch (Exception e) {
        LOGGER.error("An error occured while stopping download", e);
      }
    }
  }
  
  private class VisualizePacketAction extends AbstractAction {
    private static final long serialVersionUID = -7356442420934284553L;

    public VisualizePacketAction(){
      putValue( Action.NAME, "Details bestandsoverdracht" );
    }

    @Override
    public void actionPerformed(ActionEvent anArg0) {
      if(myPacketVisualizer == null){
        myPacketVisualizer = new PacketVisualizerFrame(myHandler);
      }
      myPacketVisualizer.setVisible(true);
    }
  }

}
