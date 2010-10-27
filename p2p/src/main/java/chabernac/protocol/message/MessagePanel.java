/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.ProtocolException;

public class MessagePanel extends JPanel {
  
  private static final long serialVersionUID = 6219310744066274813L;
  
  private MessageModel myModel;
  private String myPeerId;

  public MessagePanel(MessageProtocol aProtocol){
    try {
      myPeerId = aProtocol.getRoutingTable().getLocalPeerId();
    } catch ( ProtocolException e ) {
      myPeerId = "No peerid";
    }
    buildGUI(aProtocol);
  }
  
  private void buildGUI(MessageProtocol aProtocol){
    setLayout( new BorderLayout() );
  
    myModel = new MessageModel(aProtocol);
    JTable theTable = new JTable(myModel);
    theTable.setDefaultRenderer(String.class, new ColorRenderer());
    
    theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 50 );
    theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 50 );
    theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 220 );
    theTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 220 );
    theTable.getColumnModel().getColumn( 4 ).setPreferredWidth( 50 );
    theTable.getColumnModel().getColumn( 5 ).setPreferredWidth( 400 );
    
    add(new JScrollPane(theTable), BorderLayout.CENTER);
  }
  
  private class ColorRenderer extends JLabel implements TableCellRenderer{
    private static final long serialVersionUID = 7571899561399741995L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setOpaque( true );
      setForeground(Color.darkGray);
      MessageAndResponse theMessage = myModel.getMessageAtRow( anRow);
     
      if( (theMessage.getMessage().getSource().getPeerId().equals( myPeerId ) && anColumn == 2) ||
          (theMessage.getMessage().getDestination().getPeerId().equals( myPeerId ) && anColumn == 3) ){
        setForeground( Color.blue );
      }
      
      if(theMessage.getResponse() == null){
        setBackground( Color.YELLOW );
      } else {
        setBackground( Color.WHITE);
      }
      
      if(anValue == null) {
        setText( "" );
      } else {
        setText(  anValue.toString());
      }
      
      return this;
    }
  }

}
