package chabernac.io;

import javax.swing.JPanel;

public class StreamSplitterPoolPanel extends JPanel {

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


}
