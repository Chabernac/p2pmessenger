package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.protocol.asyncfiletransfer.FileTransferState.Direction;

public class FileTransferOverviewPanel extends JPanel implements iTransferChangeListener {
  private static final long serialVersionUID = 3820389600984173704L;
  private static Logger LOGGER = Logger.getLogger(FileTransferOverviewPanel.class);
  private final iTransferController myTransferController;

  private Map<String, FileTransferPanel> myTransferPanels = new HashMap<String, FileTransferPanel>();

  private JPanel myIncomingTransferPanel = new JPanel();
  private JPanel myOutgoingTransferPanel = new JPanel();

  public FileTransferOverviewPanel(iTransferController anTransferController) {
    super();
    myTransferController = anTransferController;
    buildGUI();
    addListeners();
    populate();
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    myIncomingTransferPanel.setLayout(new GridLayout(-1, 1, 2, 5));
    myOutgoingTransferPanel.setLayout(new GridLayout(-1, 1, 2, 5));

    myIncomingTransferPanel.setBorder(new TitledBorder("Receiving"));
    myOutgoingTransferPanel.setBorder(new TitledBorder("Sending"));

    JPanel theTransferPanel = new JPanel(new GridBagLayout());
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.weightx = 1;
    theCons.weighty = 0;
    theCons.fill = GridBagConstraints.HORIZONTAL;
    theTransferPanel.add(myIncomingTransferPanel, theCons);
    theCons.gridy++;
    theTransferPanel.add(myOutgoingTransferPanel, theCons);
    add(theTransferPanel,  BorderLayout.NORTH);
    add(buildButtonPanel(), BorderLayout.SOUTH);
  }
  
  private JPanel buildButtonPanel(){
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    theButtonPanel.add(new CommandButton(new ClearFinished()));
    return theButtonPanel;
  }

  private void populate(){
    populate(myTransferController.getReceivingTransfers());
    populate(myTransferController.getSendingTransfers());
  }

  private void populate(Set<String> aTransfers){
    for(String theTransferId : aTransfers){
      populate(theTransferId);
    }
  }

  private void populate(String aTransferId){
    try{
      if(!myTransferPanels.containsKey(aTransferId)){
        FileTransferHandler theHandler = myTransferController.getTransferHandler(aTransferId);
        Direction theDirection = theHandler.getState().getDirection();
        FileTransferPanel theTransferPanel = new FileTransferPanel(myTransferController.getTransferHandler(aTransferId));
        myTransferPanels.put(aTransferId, theTransferPanel);
        if(theDirection == Direction.RECEIVING){
          myIncomingTransferPanel.add(theTransferPanel);
        } else if(theDirection == Direction.SENDING){
          myOutgoingTransferPanel.add(theTransferPanel);
        }
      }
    }catch(AsyncFileTransferException e){
      LOGGER.error("An error occured while adding transfer panel", e);
    }
  }

  private void addListeners(){
    myTransferController.addTransferChangeListener(this);
  }

  @Override
  public void transferRemoved(final String aTransferId) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run(){
        FileTransferPanel thePanel = myTransferPanels.get(aTransferId);
        myIncomingTransferPanel.remove(thePanel);
        myOutgoingTransferPanel.remove(thePanel);
        repaint();
      }
    });
  }

  @Override
  public void transferStarted(final String aTransferId) {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        populate(aTransferId);
        revalidate();
      }
    });
  }
  
  public class ClearFinished extends AbstractCommand {
    @Override
    public String getName() {
      return "Clear finished";
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void execute() {
      myTransferController.removeFinished();
    }
  }

}
