package chabernac.p2p.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class WhoIsRunningPanel extends JPanel {
    /**
	 * 
	 */
    private static final long            serialVersionUID = 7577814509143406576L;
    private final int                    myScanIntervalTimeoutInSeconds;
    private final WhoIsRunningTableModel myTableModel;
    private final WhoIsRunning           myWhoIs;
    private static int                   START_PORT       = 12700;
    private static int                   END_PORT         = 12708;
    private final JTextField             myIpAddress      = new JTextField();

    public WhoIsRunningPanel( int anScanIntervalTimeoutInSeconds ) {
        super();

        myScanIntervalTimeoutInSeconds = anScanIntervalTimeoutInSeconds;
        myTableModel = new WhoIsRunningTableModel( START_PORT, END_PORT );
        myWhoIs = new WhoIsRunning( myTableModel, "localhost", START_PORT, END_PORT );
        buildGUI();
        startRefresh();
    }

    private void startRefresh() {
        ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
        theService.scheduleWithFixedDelay( myWhoIs, 1, myScanIntervalTimeoutInSeconds, TimeUnit.SECONDS );
    }

    private void buildGUI() {
        setLayout( new BorderLayout() );
        buildNorthPanel();
        buildCenterPanel();
    }

    private void buildNorthPanel() {
        JPanel thePanel = new JPanel( new BorderLayout() );
        thePanel.add( myIpAddress, BorderLayout.CENTER );
        thePanel.add( new JButton( new OKAction() ), BorderLayout.EAST );
        add( thePanel, BorderLayout.NORTH );
    }

    private class OKAction extends AbstractAction {
        private static final long serialVersionUID = 1606433136992589353L;

        public OKAction() {
            super( "Ok" );
            putValue( SHORT_DESCRIPTION, "Ok" );
        }

        @Override
        public void actionPerformed( ActionEvent aE ) {
            myWhoIs.setHost( myIpAddress.getText() );

        }

    }

    private void buildCenterPanel() {
        JTable theTable = new JTable( myTableModel );
        theTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
        theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 400 );
        theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 100 );
        theTable.setDefaultRenderer( String.class, new ColorRenderer() );

        add( new JScrollPane( theTable ), BorderLayout.CENTER );
    }

    private class ColorRenderer extends JLabel implements TableCellRenderer {

        private static final long serialVersionUID = 5070507133968870256L;

        @Override
        public Component getTableCellRendererComponent( JTable anTable,
                                                        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
                                                        int anColumn ) {
            setForeground( Color.black );
            WhoIsRunningTableModel.Entry theEntry = myTableModel.getPeerIdsAtPort().get( anRow );
            if ( theEntry.myState == WhoIsRunningTableModel.Entry.State.RUNNING ) {
                setForeground( Color.blue );
            }
            setText( anValue.toString() );
            return this;
        }
    }
}
