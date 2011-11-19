package chabernac.protocol.packet;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class TransferOverviewFrame extends JFrame {
  private static final long serialVersionUID = -6126711061359525985L;
  private final iTransferContainer myTransferController;

  public TransferOverviewFrame(iTransferContainer anTransferController) {
    super();
    myTransferController = anTransferController;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    add(new TransferOverviewPanel(myTransferController), BorderLayout.CENTER);
    setSize(500, 600);
    setTitle("Downloads / Uploads");
  }
}
