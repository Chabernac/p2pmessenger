package chabernac.test;

import chabernac.synchro.SimpleSynchronizedFrame;

public class TestSynchronizedFrame {

  /**
   * @param args
   */
  public static void main(String[] args) {
    SimpleSynchronizedFrame theFrame = new SimpleSynchronizedFrame("localhost", 14003, 1);
    theFrame.setSize(200, 200);
    theFrame.setLocation(200, 200);
    theFrame.setVisible(true);
  }

}
