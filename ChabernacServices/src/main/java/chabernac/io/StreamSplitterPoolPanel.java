package chabernac.io;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class StreamSplitterPoolPanel extends JPanel {
  private static final long serialVersionUID = -7337069960596111123L;
  private final StreamSplitterPool myStreamSplitterPool;
  private StreamSplitterPoolTableModel myModel;

  public StreamSplitterPoolPanel(StreamSplitterPool aStreamSplitterPool) {
    super();
    myStreamSplitterPool = aStreamSplitterPool;
    addListener();
    buildGUI();
  }

  private void addListener(){
    myStreamSplitterPool.addStreamSplitterPoolListener(new StreamSplitterPoolListener());
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    myModel = new StreamSplitterPoolTableModel();
    add(new JScrollPane(new JTable(myModel)));
  }

  public class StreamSplitterPoolListener implements
  iStreamSplitterPoolListener {

    @Override
    public void streamSplitterAdded(StreamSplitter aStreamSplitter) {
      myModel.fireTableModelChanged();
    }

    @Override
    public void streamSplitterRemoved(StreamSplitter aStreamSplitter) {
      myModel.fireTableModelChanged();
    }
  }
  
  private class StreamSplitterPoolTableModel implements TableModel{
    private List<StreamSplitter> mySplitters = null;
    private final List< TableModelListener > myListeners = new ArrayList< TableModelListener >();

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public int getRowCount() {
      mySplitters = new ArrayList<StreamSplitter>(myStreamSplitterPool.getStreamSplitters().values());
      return mySplitters.size();
    }

    @Override
    public Object getValueAt(int aRow, int aColumn) {
      StreamSplitter theSplitter = mySplitters.get(aRow);
      if(aColumn == 0)  return theSplitter.getId(); 
      if(aColumn == 1)  return theSplitter.getBytesReceived();
      if(aColumn == 2)  return theSplitter.getBytesSend();
      if(aColumn == 3)  return !theSplitter.isClosed();
      return "";
    }

    @Override
    public void addTableModelListener(TableModelListener aListener) {
      myListeners.add(aListener);
    }
    
    @Override
    public void removeTableModelListener(TableModelListener aListener) {
      myListeners.remove(aListener);
    }
    
    public void fireTableModelChanged(){
      for(TableModelListener theListener : myListeners){
        theListener.tableChanged(new TableModelEvent(this));
      }
    }

    @Override
    public Class<?> getColumnClass(int aArg0) {
      return String.class;
    }

    @Override
    public String getColumnName(int aColumn) {
      if(aColumn == 0) return "ID";
      if(aColumn == 1) return "Bytes send";
      if(aColumn == 2) return "Bytes received";
      if(aColumn == 3) return "Active";
      return "";
    }

    @Override
    public boolean isCellEditable(int aArg0, int aArg1) {
      return false;
    }


    @Override
    public void setValueAt(Object aArg0, int aArg1, int aArg2) {
    }
  }


}
