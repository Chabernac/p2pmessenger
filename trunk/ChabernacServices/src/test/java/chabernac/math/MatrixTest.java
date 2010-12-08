package chabernac.math;

import junit.framework.TestCase;

public class MatrixTest extends TestCase{
  public void testMultiply(){
    Matrix theMatrix = new Matrix(2,3);
    theMatrix.setSource(new float[]{1,2,3,4,5,6});
    
    Matrix theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new float[]{7,8,9,10,11,12});
    
    Matrix theNewMatrix = theMatrix.multiply(  theOtherMatrix);
    
    System.out.println(theNewMatrix.toString());
    
    assertEquals(58F, theNewMatrix.getValueAt(0, 0));
    assertEquals(64F, theNewMatrix.getValueAt(0, 1));
    assertEquals(139F, theNewMatrix.getValueAt(1, 0));
    assertEquals(154F, theNewMatrix.getValueAt(1, 1));
    
    theMatrix = new Matrix(1,3);
    theMatrix.setSource(new float[]{1,2,3});
    
    theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new float[]{7,8,9,10,11,12});
    
    theNewMatrix = theMatrix.multiply(  theOtherMatrix);
    
    System.out.println(theNewMatrix.toString());
    
    assertEquals(58F, theNewMatrix.getValueAt(0, 0));
    assertEquals(64F, theNewMatrix.getValueAt(0, 1));
  }
  
  public void testMultiply2(){
    Matrix theMatrix = new Matrix(2,3);
    theMatrix.setSource(new float[]{1,2,3,4,5,6});
    
    Matrix theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new float[]{7,8,9,10,11,12});
    
    Matrix theNewMatrix = theMatrix.multiply2(  theOtherMatrix);
    
    System.out.println(theNewMatrix.toString());
    
    assertEquals(58F, theNewMatrix.getValueAt(0, 0));
    assertEquals(64F, theNewMatrix.getValueAt(0, 1));
    assertEquals(139F, theNewMatrix.getValueAt(1, 0));
    assertEquals(154F, theNewMatrix.getValueAt(1, 1));
    
    theMatrix = new Matrix(1,3);
    theMatrix.setSource(new float[]{1,2,3});
    
    theOtherMatrix = new Matrix(3,2);
    theOtherMatrix.setSource(new float[]{7,8,9,10,11,12});
    
    theNewMatrix = theMatrix.multiply2(  theOtherMatrix);
    
    System.out.println(theNewMatrix.toString());
    
    assertEquals(58F, theNewMatrix.getValueAt(0, 0));
    assertEquals(64F, theNewMatrix.getValueAt(0, 1));
  }
  
  private float testMatrixMultiplyPerformance(Matrix aMatrix1, Matrix aMatrix2){
    aMatrix1.setValueAt( 0, 0, 1 );
    aMatrix1.setValueAt( 1, 0, 2 );
    aMatrix1.setValueAt( 0, 1, 3 );
    aMatrix1.setValueAt( 1, 1, 4 );
    aMatrix1.setValueAt( 0, 2, 5 );
    aMatrix1.setValueAt( 1, 2, 6 );
    
    aMatrix2.setValueAt( 0, 0, 7 );
    aMatrix2.setValueAt( 1, 0, 8 );
    aMatrix2.setValueAt( 2, 0, 9 );
    aMatrix2.setValueAt( 0, 1, 10 );
    aMatrix2.setValueAt( 1, 1, 11 );
    aMatrix2.setValueAt( 2, 1, 12 );
    
    int times = 50000000;
    
    long theStartTime = System.currentTimeMillis();
    
    for(int i=0;i<times;i++){
      aMatrix1.multiply(aMatrix2);
    }
    
    long theEndTime = System.currentTimeMillis();
    
    return times / (theEndTime - theStartTime);
  }
  
  private float testMatrixMultiplyPerformance2(Matrix aMatrix1, Matrix aMatrix2){
    aMatrix1.setValueAt( 0, 0, 1 );
    aMatrix1.setValueAt( 1, 0, 2 );
    aMatrix1.setValueAt( 0, 1, 3 );
    aMatrix1.setValueAt( 1, 1, 4 );
    aMatrix1.setValueAt( 0, 2, 5 );
    aMatrix1.setValueAt( 1, 2, 6 );
    
    aMatrix2.setValueAt( 0, 0, 7 );
    aMatrix2.setValueAt( 1, 0, 8 );
    aMatrix2.setValueAt( 2, 0, 9 );
    aMatrix2.setValueAt( 0, 1, 10 );
    aMatrix2.setValueAt( 1, 1, 11 );
    aMatrix2.setValueAt( 2, 1, 12 );
    
    int times = 50000000;
    
    long theStartTime = System.currentTimeMillis();
    
    for(int i=0;i<times;i++){
      aMatrix1.multiply2(aMatrix2);
    }
    
    long theEndTime = System.currentTimeMillis();
    
    return times / (theEndTime - theStartTime);
  }
  
  private float testMatrixMultiplyPerformance(Matrix2 aMatrix1, Matrix2 aMatrix2){
    aMatrix1.setValueAt( 0, 0, 1 );
    aMatrix1.setValueAt( 1, 0, 2 );
    aMatrix1.setValueAt( 0, 1, 3 );
    aMatrix1.setValueAt( 1, 1, 4 );
    aMatrix1.setValueAt( 0, 2, 5 );
    aMatrix1.setValueAt( 1, 2, 6 );
    
    aMatrix2.setValueAt( 0, 0, 7 );
    aMatrix2.setValueAt( 1, 0, 8 );
    aMatrix2.setValueAt( 2, 0, 9 );
    aMatrix2.setValueAt( 0, 1, 10 );
    aMatrix2.setValueAt( 1, 1, 11 );
    aMatrix2.setValueAt( 2, 1, 12 );
    
    int times = 50000000;
    
    long theStartTime = System.currentTimeMillis();
    
    for(int i=0;i<times;i++){
      aMatrix1.multiply(aMatrix2);
    }
    
    long theEndTime = System.currentTimeMillis();
    
    return times / (theEndTime - theStartTime);
  }
  
  
  public void testPerformance(){
    Matrix theMatrix = new Matrix(2,3);
    Matrix theOtherMatrix = new Matrix(3,2);
    
    System.out.println( testMatrixMultiplyPerformance(theMatrix, theOtherMatrix) + " matrix multiplications per ms with multiply");
    
    System.out.println( testMatrixMultiplyPerformance2(theMatrix, theOtherMatrix) + " matrix multiplications per ms with multiply 2");
  }
   
} 
