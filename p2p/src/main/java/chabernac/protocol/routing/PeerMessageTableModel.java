package chabernac.protocol.routing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PeerMessageTableModel implements TableModel, iSocketPeerSenderListener {
  private final PeerSender mySocketPeerSender;
  private static SimpleDateFormat FORMAT  = new SimpleDateFormat("HH:mm:ss"); 
  
  private List<TableModelListener> myTableModelListeners = new ArrayList<TableModelListener>();
  
  public PeerMessageTableModel(PeerSender anSocketPeerSender) {
    super();
    mySocketPeerSender = anSocketPeerSender;
    mySocketPeerSender.addPeerSenderListener(this);
  }

  @Override
  public void addTableModelListener(TableModelListener aTableModelListener) {
    myTableModelListeners.add(aTableModelListener);
  }

  @Override
  public Class<?> getColumnClass(int anArg0) {
   return String.class;
  }

  @Override
  public int getColumnCount(){ 
    return 5;
  }

  @Override
  public String getColumnName(int aColumn) {
   if(aColumn == 0) return "Time";
   if(aColumn == 1) return "State";
   if(aColumn == 2) return "To";
   if(aColumn == 3) return "Input";
   if(aColumn == 4) return "Response";
   return "";
  }

  @Override
  public int getRowCount() {
    return mySocketPeerSender.getHistory().size();
  }
  
  public PeerMessage getPeerMessageAtRow(int aRow){
    return mySocketPeerSender.getHistory().get(aRow);
  }

  @Override
  public Object getValueAt(int aRow, int aColumn) {
    PeerMessage theMessage = getPeerMessageAtRow(aRow);
    if(aColumn == 0) {
      Date theDate = new Date();
      theDate.setTime( theMessage.getCreationTimestamp() );
      return FORMAT.format( theDate );
    }
    if(aColumn == 1) return theMessage.getState().name();
    if(aColumn == 2) {
      if(theMessage.getPeer().getPeerId() != null && !"".equals( theMessage.getPeer().getPeerId())) return theMessage.getPeer().getPeerId();
      else return theMessage.getPeer().getEndPointRepresentation();
    }
    if(aColumn == 3) return theMessage.getMessage();
    if(aColumn == 4) return theMessage.getResult();
    return null;
  }

  @Override
  public boolean isCellEditable(int anArg0, int anArg1) {
    return false;
  }

  @Override
  public void removeTableModelListener(TableModelListener aTableModelListener) {
    myTableModelListeners.remove(aTableModelListener);
  }

  @Override
  public void setValueAt(Object anArg0, int anArg1, int anArg2) {

  }

  @Override
  public void messageStateChanged(PeerMessage aMessage) {
    for(TableModelListener theListener : myTableModelListeners){
      theListener.tableChanged(new TableModelEvent(this));
    }
  }
}
