package chabernac.protocol.routing;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import chabernac.tools.TestTools;

public class WhoIsFrame extends JFrame {
  private static final long serialVersionUID = 4991947660693720494L;

  public WhoIsFrame(){
    buildGUI();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  
  private void buildGUI(){
    setTitle("Who is");
    setLayout( new BorderLayout());
    add(new WhoIsRunningPanel(1, "localhost"), BorderLayout.CENTER);
    setSize(400, 400);
  }
  
  public static void main(String args[]){
    if(args.length > 0){
      TestTools.setIsUnitTest( "true".equals( args[0] ) );
    }
    WhoIsFrame theFrame = new WhoIsFrame();
    theFrame.setVisible(true);
  }

}
