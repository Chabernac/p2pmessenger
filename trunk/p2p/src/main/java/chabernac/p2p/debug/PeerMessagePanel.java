package chabernac.p2p.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.routing.PeerMessage;
import chabernac.protocol.routing.PeerSender;
import chabernac.protocol.routing.iPeerSender;
import chabernac.protocol.routing.iSocketPeerSenderListener;
import chabernac.protocol.routing.PeerMessage.State;

public class PeerMessagePanel extends JPanel {
  private static final long serialVersionUID = 1872558355864705752L;
  private static NumberFormat FORMAT = NumberFormat.getInstance();

  static{
    FORMAT.setMaximumFractionDigits( 2 );
    FORMAT.setMinimumFractionDigits( 2 );
  }

  private final PeerSender myPeerSender;

  private JTextField myBytesReceived = new JTextField(12);
  private JTextField myBytesSend = new JTextField(12);
  private JTextField myBandWithUp = new JTextField(12);
  private JTextField myBandWithDown = new JTextField(12);


  public PeerMessagePanel ( iPeerSender aPeerSender ) {
    myPeerSender = (PeerSender)aPeerSender;
    buildGUI();
  }

  private void buildGUI(){
    setLayout( new BorderLayout() );
    PeerMessageTableModel theModel = new PeerMessageTableModel(myPeerSender);
    JTable theTable = new JTable(theModel);
    theTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 30 );
    theTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 10 );
    theTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 200 );
    theTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 400 );
    theTable.getColumnModel().getColumn( 4 ).setPreferredWidth( 400 );

    ColorRenderer theRenderer = new ColorRenderer(theModel);
    theTable.setDefaultRenderer(String.class, theRenderer);
    theTable.setDefaultRenderer(Integer.class, theRenderer);
    add(new JScrollPane(theTable), BorderLayout.CENTER);

    add(buildBandWithPanel(), BorderLayout.SOUTH);
    
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleAtFixedRate( new Runnable(){
      public void run(){
        calculateBandWith();
      }
    }, 5, 5, TimeUnit.SECONDS);

  }

  private JPanel buildBandWithPanel(){
    myPeerSender.addPeerSenderListener( new BandWithCalculator() );
    JPanel thePanel = new JPanel(new GridBagLayout());

    GridBagConstraints theCons = new GridBagConstraints();
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.weightx = 0;
    theCons.weighty = 0;
    theCons.fill = GridBagConstraints.NONE;
    theCons.anchor = GridBagConstraints.WEST;
    thePanel.add(new JLabel("Received"), theCons);
    
    theCons.gridx++;
    thePanel.add(myBytesReceived, theCons);
    
    theCons.gridx++;
    thePanel.add(new JLabel("bytes"), theCons);

    theCons.gridx = 0;
    theCons.gridy++;
    thePanel.add(new JLabel("Send"), theCons);

    theCons.gridx++;
    thePanel.add(myBytesSend, theCons);
    
    theCons.gridx++;
    thePanel.add(new JLabel("bytes"), theCons);

    theCons.gridx = 0;
    theCons.gridy++;
    thePanel.add(new JLabel("Bandwidth up"), theCons);

    theCons.gridx++;
    thePanel.add(myBandWithUp, theCons);

    theCons.gridx++;
    thePanel.add(new JLabel("kbyte/s"), theCons);

    theCons.gridx = 0;
    theCons.gridy++;
    thePanel.add(new JLabel("Bandwidth down"), theCons);

    theCons.gridx++;
    thePanel.add(myBandWithDown, theCons);

    theCons.gridx++;
    thePanel.add(new JLabel("kbyte/s"), theCons);

    return thePanel;
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

  private void calculateBandWith(){
    myBytesReceived.setText( Long.toString( myPeerSender.getBytesReceived() ));
    myBytesSend.setText( Long.toString(myPeerSender.getBytesSend() ));

    float theTime = System.currentTimeMillis() - myPeerSender.getInitTime();

    float theKBPerSecUp = (float)myPeerSender.getBytesSend() / theTime;
    float theKBPerSecDown = (float)myPeerSender.getBytesReceived() / theTime;

    myBandWithUp.setText( FORMAT.format( theKBPerSecUp ) );
    myBandWithDown.setText( FORMAT.format( theKBPerSecDown ) );  
  }

  public class BandWithCalculator implements iSocketPeerSenderListener {

    @Override
    public void messageStateChanged( PeerMessage aMessage ) {
      calculateBandWith();
    }

  }
}
