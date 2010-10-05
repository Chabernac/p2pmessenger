package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.routing.PeerMessage.State;

public class PeerMessagePanel extends JPanel {
  private static final long serialVersionUID = 1872558355864705752L;
  private final iPeerSender myPeerSender;
  
  
  public PeerMessagePanel ( iPeerSender aPeerSender ) {
    myPeerSender = aPeerSender;
    buildGUI();
  }

  private void buildGUI(){
    setLayout( new BorderLayout() );
    PeerMessageTableModel theModel = new PeerMessageTableModel((PeerSender) myPeerSender);
    JTable theTable = new JTable(theModel);
    theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 10 );
    theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 200 );
    theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 400 );
    theTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 400 );
    
    ColorRenderer theRenderer = new ColorRenderer(theModel);
    theTable.setDefaultRenderer(String.class, theRenderer);
    theTable.setDefaultRenderer(Integer.class, theRenderer);
    add(new JScrollPane(theTable), BorderLayout.CENTER);
    
   
  }
  
  private class ColorRenderer extends JLabel implements TableCellRenderer{
    private static final long serialVersionUID = 7571899561399741995L;
    private final PeerMessageTableModel myModel;
    
    public ColorRenderer(PeerMessageTableModel anModel) {
      myModel = anModel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setOpaque( true );
      setBackground( Color.white );
      
      PeerMessage theMessage = myModel.getPeerMessageAtRow(anRow);
     
      if(theMessage.getState() == State.INIT) setBackground( Color.yellow );
      else if(theMessage.getState() == State.OK) setBackground( new Color(150, 150, 255));
      else if(theMessage.getState() == State.NOK) setBackground( new Color(255, 150, 150));
            
      if(anValue != null){
        setText(anValue.toString());
      } else {
        setText("");
      }
      
      return this;
    }
  }
}
