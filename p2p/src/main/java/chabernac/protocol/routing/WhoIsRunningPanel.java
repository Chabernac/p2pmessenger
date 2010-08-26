package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class WhoIsRunningPanel extends JPanel {
  private final int myScanIntervalTimeoutInSeconds;
  private final String myHost;
  private final WhoIsRunningTableModel myTableModel;
  private final WhoIsRunning myWhoIs;
  
  public WhoIsRunningPanel(int anScanIntervalTimeoutInSeconds, String anHost) {
    super();
    myScanIntervalTimeoutInSeconds = anScanIntervalTimeoutInSeconds;
    myHost = anHost;
    myTableModel = new WhoIsRunningTableModel(RoutingProtocol.START_PORT, RoutingProtocol.END_PORT);
    myWhoIs = new WhoIsRunning(myTableModel, "localhost", RoutingProtocol.START_PORT, RoutingProtocol.END_PORT);
    buildGUI();
    startRefresh();
  }
  
  private void startRefresh(){
    ScheduledExecutorService theService = Executors.newScheduledThreadPool(1);
    theService.scheduleAtFixedRate(myWhoIs, 1, myScanIntervalTimeoutInSeconds, TimeUnit.SECONDS);
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    buildCenterPanel();
  }
  
  private void buildCenterPanel(){
    JTable theTable= new JTable(myTableModel);
    theTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
    theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
    theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 400 );
    theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 100 );
    theTable.setDefaultRenderer(String.class, new ColorRenderer());
    
    add(new JScrollPane(theTable), BorderLayout.CENTER);
  }
  
  private class ColorRenderer extends JLabel implements TableCellRenderer{

    private static final long serialVersionUID = 5070507133968870256L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setForeground(Color.black);
      WhoIsRunningTableModel.Entry theEntry = myTableModel.getPeerIdsAtPort().get(anRow);
      if(theEntry.myState == WhoIsRunningTableModel.Entry.State.RUNNING){
        setForeground(Color.blue);
      }
      setText(anValue.toString());
      return this;
    }
  }
}
