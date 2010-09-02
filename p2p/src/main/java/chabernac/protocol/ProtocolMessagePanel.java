package chabernac.protocol;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ProtocolMessagePanel extends JPanel {
  
  private static final long serialVersionUID = -1776466922176101894L;

  public ProtocolMessagePanel(ProtocolContainer aContainer){
    buildGUI(aContainer);
  }
  
  private void buildGUI(ProtocolContainer aContainer){
    setLayout(new BorderLayout());
    add(new JScrollPane(new JTable(new ProtocolMessageModel(aContainer))), BorderLayout.CENTER);
  }

}
