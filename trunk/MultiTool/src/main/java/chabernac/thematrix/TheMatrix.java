package chabernac.thematrix;

import java.awt.Color;

public class TheMatrix implements Runnable{
  private Color myNewCharColor = new Color(0x00A937);
  private Color myOldCharColor = new Color(0x1EA93F);
  private int[] myScreenTop;
  private int[] myStartOrder;
  private int[] myStartOrderOffset;
  
  private char[][] myMatrix;
  
  public TheMatrix(int rows, int cols){
    myMatrix = new char[cols][rows];
    myScreenTop = new int[cols];
    myStartOrder = new int[cols];
    myStartOrderOffset = new int[cols];
  }

  public void run() {
    randomize();
  }
  
  private void randomize(){
    
  }

}
