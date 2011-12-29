package chabernac.protocol.packet;

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

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.protocol.packet.AbstractTransferState.Direction;

public class TransferOverviewPanel extends JPanel implements iTransferListener {
  private static final long serialVersionUID = 3820389600984173704L;
  //  private static Logger LOGGER = Logger.getLogger(TransferOverviewPanel.class);
  private final iTransferContainer myTransferController;

  private Map<String, TransferPanel> myTransferPanels = new HashMap<String, TransferPanel>();

  private JPanel myIncomingTransferPanel = new JPanel();
  private JPanel myOutgoingTransferPanel = new JPanel();

  public TransferOverviewPanel(iTransferContainer anTransferController) {
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
    populate(myTransferController.getTransferStates());
  }

  private void populate(Set<AbstractTransferState> aTransfers){
    for(AbstractTransferState theTransfer : aTransfers){
      populate(theTransfer);
    }
  }

  private void populate(AbstractTransferState aTransfer){
    if(!myTransferPanels.containsKey(aTransfer.getTransferId())){
      Direction theDirection = aTransfer.getTransferState().getDirection();
      TransferPanel theTransferPanel = new TransferPanel(aTransfer);
      myTransferPanels.put(aTransfer.getTransferId(), theTransferPanel);
      if(theDirection == Direction.RECEIVE){
        myIncomingTransferPanel.add(theTransferPanel);
      } else if(theDirection == Direction.SEND){
        myOutgoingTransferPanel.add(theTransferPanel);
      }
    }
  }

  private void addListeners(){
    myTransferController.addTransferListener( this );
  }

  @Override
  public void newTransfer( AbstractTransferState aTransfer, boolean isIncoming  ) {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        populate(myTransferController.getTransferStates());
        revalidate();
      }
    });
  }

  @Override
  public void transferRemoved( AbstractTransferState aTransfer ) {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        populate(myTransferController.getTransferStates());
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
      //myTransferController.removeFinished();
    }
  }
}
