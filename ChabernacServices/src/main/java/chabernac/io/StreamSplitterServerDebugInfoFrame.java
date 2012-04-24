package chabernac.io;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class StreamSplitterServerDebugInfoFrame extends JFrame {
  private final StreamSplittingServer myServer;

  public StreamSplitterServerDebugInfoFrame(StreamSplittingServer aServer) throws HeadlessException {
    super();
    myServer = aServer;
    buildGUI();
    setSize(800, 300);
  }
  
  private void buildGUI(){
    getContentPane().setLayout(new BorderLayout());
    add(new StreamSplitterPoolPanel(myServer.getStreamSplitterPool()));
  }
  
  
  

}
