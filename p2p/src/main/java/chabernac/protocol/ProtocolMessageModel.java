package chabernac.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ProtocolMessageModel implements TableModel {
  private final ProtocolContainer myContainer;

  private List<TableModelListener> myListeners = new ArrayList<TableModelListener>();
  
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

  public ProtocolMessageModel(ProtocolContainer anContainer) {
    super();
    myContainer = anContainer;
    myContainer.addProtocolMessageListener(new ProtocolMessageListener());
  }

  @Override
  public void addTableModelListener(TableModelListener aListener) {
    myListeners.add(aListener);
  }

  @Override
  public Class<?> getColumnClass(int anArg0) {
   return String.class;
  }

  @Override
  public int getColumnCount() {
    return 5;
  }

  @Override
  public String getColumnName(int aColumn) {
    if(aColumn == 0) return "Timestamp";
    if(aColumn == 1) return "State";
    if(aColumn == 2) return "Input";
    if(aColumn == 3) return "Output";
    if(aColumn == 4) return "Response time";
    return "";
  }

  @Override
  public int getRowCount() {
    return myContainer.getMessageHistory().size();
  }
  
  public ProtocolMessageEntry getEntryAtRow(int aRow){
    return myContainer.getMessageHistory().get(aRow);
  }

  @Override
  public Object getValueAt(int aRow, int aColumn) {
    ProtocolMessageEntry theEntry = getEntryAtRow( aRow );
    if(aColumn == 0) return FORMAT.format(theEntry.getTimestamp());
    if(aColumn == 1) return theEntry.getState().name();
    if(aColumn == 2) return theEntry.getInput();
    if(aColumn == 3) return theEntry.getOutput();
    if(aColumn == 4) return ((double)theEntry.getResponseTime()) / 1000D;
    return null;
  }

  @Override
  public boolean isCellEditable(int anArg0, int anArg1) {
    return false;
  }

  @Override
  public void removeTableModelListener(TableModelListener aListener) {
    myListeners.remove(aListener);
  }

  @Override
  public void setValueAt(Object anArg0, int anArg1, int anArg2) {

  }
  
  private class ProtocolMessageListener implements iProtocolMessageListener {

    @Override
    public void messageReceived() {
      for(TableModelListener theListener : myListeners){
        theListener.tableChanged(new TableModelEvent(ProtocolMessageModel.this));
      }
    }

  }

}
