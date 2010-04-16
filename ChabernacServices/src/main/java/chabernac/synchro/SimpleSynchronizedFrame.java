package chabernac.synchro;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class SimpleSynchronizedFrame extends JFrame {
  private static final long serialVersionUID = 7160840305104528596L;
  private String myServer;
  private int myPort;
  private int myPlayer;
  
  public SimpleSynchronizedFrame(String aServer, int aPort, int aPlayer){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    myServer = aServer;
    myPlayer = aPlayer;
    myPort = aPort;
    buildGUI();
  }
  
  private void buildGUI(){
    SimpleSynchronizedPanel thePanel = new SimpleSynchronizedPanel(myServer, myPort, myPlayer);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(thePanel, BorderLayout.CENTER);
  }

}
