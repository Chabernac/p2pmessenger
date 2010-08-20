/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;

public class SocketPoolPanel extends JPanel implements Observer {
  private static final long serialVersionUID = 2297825614322988606L;
  private JTable myTable = null;
  private SocketPoolModel myModel;
  
  public SocketPoolPanel(){
    buildGUI();
    addListeners();
  }
  
  private void buildGUI(){
    setLayout(  new BorderLayout() );
    myModel =new SocketPoolModel(SocketPool.getInstance( ));
    myTable = new JTable(myModel);
    myTable.setDefaultRenderer(String.class, new ColorRenderer());
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    setBorder( new TitledBorder("Cached sockets") );
    add(buildButtonPanel(), BorderLayout.SOUTH);
  }
  
  private JPanel buildButtonPanel(){
    JPanel thePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    thePanel.add(new JButton(new CleanUp()));
    return thePanel;
  }
  
  private void addListeners(){
    SocketPool.getInstance( ).addObserver( this );
  }

  @Override
  public void update( Observable anO, Object anArg ) {
    myTable.tableChanged( new TableModelEvent(myTable.getModel()) );
  }
  
  private class ColorRenderer extends JLabel implements TableCellRenderer{

    private static final long serialVersionUID = 7571899561399741995L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setForeground(Color.darkGray);
      SocketProxy theProxy = myModel.getSocketProxyAtRow(anRow);
     
      setText(anValue.toString());
      
      if(theProxy == null) return this;
      
      String thePool = myModel.getPool(theProxy);
      if("IN".equals(thePool)) setForeground(Color.BLUE);
      if("OUT".equals(thePool)) setForeground(Color.ORANGE);
      if("CONNECT".equals(thePool)) setForeground(Color.GREEN);
      
      setToolTipText(parseToHtml(theProxy.getStackTrace()));
      return this;
    }

    private String parseToHtml(String aString){
      aString = aString.replaceAll("java.lang.Exception\r\n", "");
      StringBuilder theBuilder = new StringBuilder();
      theBuilder.append("<html>");
      theBuilder.append(aString.replaceAll("\r\n", "<br>"));
      theBuilder.append("</html>");
      return theBuilder.toString();
    }

    private boolean listContainsObject(List<? extends Object> aList, Object anObject){
      for(Object theObject : aList){
        if(theObject == anObject) return true;
      }
      return false;
    }
  }
  
  public class CleanUp  extends AbstractAction{
    public CleanUp(){
      putValue( Action.NAME, "Cleanup" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      SocketPool.getInstance( ).cleanUp();
    }
  }  
}
