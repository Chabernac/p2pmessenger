package chabernac.math;

import junit.framework.TestCase;

public class MatrixTest extends TestCase{
  public void testMultiply(){
    Matrix theMatrix = new Matrix(2,3);
    theMatrix.setSource(new double[]{1,2,3,4,5,6});
    
    Matrix theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new double[]{7,8,9,10,11,12});
    
    Matrix theNewMatrix = theMatrix.multiply( theOtherMatrix);
    
    System.out.println(theNewMatrix.toString());
    
    assertEquals(58D, theNewMatrix.getValueAt(0, 0));
    assertEquals(64D, theNewMatrix.getValueAt(0, 1));
    assertEquals(139D, theNewMatrix.getValueAt(1, 0));
    assertEquals(154D, theNewMatrix.getValueAt(1, 1));
  }
  
  public void testPerformance(){
    Matrix theMatrix = new Matrix(2,3);
    theMatrix.setSource(new double[]{1,2,3,4,5,6});
    
    Matrix theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new double[]{7,8,9,10,11,12});
    
    int times = 50000000;
    
    long theStartTime = System.currentTimeMillis();
    
    for(int i=0;i<times;i++){
      theMatrix.multiply(theOtherMatrix);
    }
    
    long theEndTime = System.currentTimeMillis();
    
    System.out.println( times / (theEndTime - theStartTime)  + " matrix multiplications per ms"); 
  }
} 
