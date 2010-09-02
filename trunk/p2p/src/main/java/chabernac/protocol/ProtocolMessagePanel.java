package chabernac.protocol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.ProtocolMessageEntry.Status;

public class ProtocolMessagePanel extends JPanel {
  private static final long serialVersionUID = -1776466922176101894L;

  private JTable myTable;
  private ProtocolMessageModel myModel;

  public ProtocolMessagePanel(ProtocolContainer aContainer){
    buildGUI(aContainer);
  }

  private void buildGUI(ProtocolContainer aContainer){
    setLayout(new BorderLayout());
    myModel = new ProtocolMessageModel(aContainer);
    myTable = new JTable(myModel);
    myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    myTable.setDefaultRenderer(String.class, new ColorRenderer());
    
    myTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 50 );
    myTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 50 );
    myTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 500 );
    myTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 500 );
    
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    myTable.addMouseListener( new MyMouseListener() );
  }


  private class MyMouseListener extends MouseAdapter{

    @Override
    public void mouseClicked( MouseEvent anEvent ) {
      if(anEvent.getClickCount() > 1){
        ProtocolMessageEntry theEntry = myModel.getEntryAtRow( myTable.getSelectedRow());
        JTextArea theArea = new JTextArea();
        theArea.setColumns( 40 );
        theArea.setWrapStyleWord( true );
        theArea.setLineWrap( true );
        theArea.setRows( 50 );
        theArea.setText( "IN: " + theEntry.getInput()  + "\r\n\r\n" + "OUT: " + theEntry.getOutput() );
        JOptionPane.showMessageDialog( ProtocolMessagePanel.this, new JScrollPane(theArea));
      }
    }
  }

 

  private class ColorRenderer extends JLabel implements TableCellRenderer{
    private static final long serialVersionUID = 7571899561399741995L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      
      setOpaque( true );
      
      if(isSelected) {
        setBackground( Color.lightGray );
      } else {
        setBackground( Color.white );
      }
        
      
      setForeground(Color.darkGray);
      ProtocolMessageEntry theMessageEntry = myModel.getEntryAtRow(anRow);
     
      setText(anValue == null ? "" : anValue.toString());
      
      if(theMessageEntry.getState() == Status.INPROGRESS) setForeground(Color.orange);
      if(theMessageEntry.getState() == Status.FINISHED) setForeground(new Color(0,100,0));
      if(theMessageEntry.getState() == Status.INVALID) setForeground(new Color(200,0,0));
      
      return this;
    }
  }
}
