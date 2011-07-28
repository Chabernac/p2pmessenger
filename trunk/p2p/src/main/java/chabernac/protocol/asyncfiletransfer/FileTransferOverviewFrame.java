package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FileTransferOverviewFrame extends JFrame {
  private static final long serialVersionUID = -6126711061359525985L;
  private final iTransferController myTransferController;

  public FileTransferOverviewFrame(iTransferController anTransferController) {
    super();
    myTransferController = anTransferController;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    add(new FileTransferOverviewPanel(myTransferController), BorderLayout.CENTER);
    setSize(400, 600);
    setTitle("Downloads / Uploads");
  }
}
