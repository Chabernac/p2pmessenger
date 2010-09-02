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
    return 4;
  }

  @Override
  public String getColumnName(int aColumn) {
    if(aColumn == 0) return "State";
    if(aColumn == 1) return "Timestamp";
    if(aColumn == 2) return "Input";
    if(aColumn == 3) return "Output";
    return "";
  }

  @Override
  public int getRowCount() {
    return myContainer.getMessageHistory().size();
  }

  @Override
  public Object getValueAt(int aRow, int aColumn) {
    ProtocolMessageEntry theEntry = myContainer.getMessageHistory().get(aRow);
    if(aColumn == 0) return theEntry.getState().name();
    if(aColumn == 1) return FORMAT.format(theEntry.getTimestamp());
    if(aColumn == 2) return theEntry.getInput();
    if(aColumn == 3) return theEntry.getOutput();
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
