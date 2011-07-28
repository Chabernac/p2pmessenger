package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class FileTransferOverviewPanel extends JPanel implements iTransferChangeListener {
  private static final long serialVersionUID = 3820389600984173704L;
  private static Logger LOGGER = Logger.getLogger(FileTransferOverviewPanel.class);
  private final iTransferController myTransferController;
  
  private Map<String, FileTransferPanel> myTransferPanels = new HashMap<String, FileTransferPanel>();
  
  private JPanel myTransferPanel = new JPanel();
  
  public FileTransferOverviewPanel(iTransferController anTransferController) {
    super();
    myTransferController = anTransferController;
    buildGUI();
    addListeners();
    populate();
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    myTransferPanel.setLayout(new GridLayout(-1, 1, 2, 5));
    add(myTransferPanel, BorderLayout.NORTH);
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
        FileTransferPanel theTransferPanel = new FileTransferPanel(myTransferController.getTransferHandler(aTransferId));
        myTransferPanels.put(aTransferId, theTransferPanel);
        myTransferPanel.add(theTransferPanel);
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
        myTransferPanel.remove(myTransferPanels.get(aTransferId));
        myTransferPanels.remove(aTransferId);
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
}
