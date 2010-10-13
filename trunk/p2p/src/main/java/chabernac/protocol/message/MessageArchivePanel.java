/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.ProtocolException;

public class MessageArchivePanel extends JPanel {

  private final MessageArchive myMesageArchive;
  private MessageArchiveTableModel myModel = null;
  private final MultiPeerMessageProtocol myProtocol;
  private final String myLocalPeerId;

  public MessageArchivePanel ( MultiPeerMessageProtocol aMultiPeerMessageProtocol) throws ProtocolException {
    super();
    myProtocol = aMultiPeerMessageProtocol;
    myMesageArchive = new MessageArchive(myProtocol);
    myLocalPeerId = myProtocol.getRoutingTable().getLocalPeerId();
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(  new BorderLayout() );
    
    buildCenterPanel();
    buildSouthPanel();
  }

  private void buildSouthPanel() {
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    theButtonPanel.add(new JButton(new RefreshAction()));
    add(theButtonPanel, BorderLayout.SOUTH);
  }

  private void buildCenterPanel() {
    myModel = new MessageArchiveTableModel(myMesageArchive);
    JTable theTable = new JTable(myModel);
    add(new JScrollPane(theTable));
    
    theTable.setDefaultRenderer(String.class, new ColorRenderer());
    
    theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 20 );
    theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 20 );
    theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 20);
    theTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 20 );
    theTable.getColumnModel().getColumn( 4 ).setPreferredWidth( 400 );
    theTable.getColumnModel().getColumn( 5 ).setPreferredWidth( 50 );
    theTable.getColumnModel().getColumn( 6 ).setPreferredWidth( 50 );
    
  }
  
  public class RefreshAction extends AbstractAction {
    public RefreshAction(){
      putValue( Action.NAME, "Refresh" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myModel.refresh();
    }
  }
  
  private class ColorRenderer extends JLabel implements TableCellRenderer{
    private static final long serialVersionUID = 7571899561399741995L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setForeground(Color.darkGray);
      MultiPeerMessage theMessage = myModel.getMessageAtRow( anRow);
     
      if(theMessage.getSource().equals( myLocalPeerId ) && anColumn == 2){
        setForeground( Color.blue );
      }
      
      setText(anValue.toString());
      
      return this;
    }
  }
}
