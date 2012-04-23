package chabernac.io;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class StreamSplitterPoolPanel extends JPanel {
  private static final long serialVersionUID = -7337069960596111123L;
  private final StreamSplitterPool myStreamSplitterPool;

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
    add(new JScrollPane(new JTable(new StreamSplitterPoolTableModel())));
  }

  public class StreamSplitterPoolListener implements
  iStreamSplitterPoolListener {

    @Override
    public void streamSplitterAdded(StreamSplitter aStreamSplitter) {
      // TODO Auto-generated method stub

    }

    @Override
    public void streamSplitterRemoved(StreamSplitter aStreamSplitter) {
      // TODO Auto-generated method stub

    }
  }
  
  private class StreamSplitterPoolTableModel extends AbstractTableModel{
    private List<StreamSplitter> mySplitters = null;

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public int getRowCount() {
      mySplitters = new ArrayList<StreamSplitter>(myStreamSplitterPool.getStreamSplitters().values());
      return mySplitters.size();
    }

    @Override
    public Object getValueAt(int aRow, int aColumn) {
      StreamSplitter theSplitter = mySplitters.get(aRow);
      if(aColumn == 0){
        return theSplitter.getId();
      }
      return "";
    }
    
  }


}
