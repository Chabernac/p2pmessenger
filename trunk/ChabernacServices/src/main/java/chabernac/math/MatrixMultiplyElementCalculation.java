/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.math;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;


public class MatrixMultiplyElementCalculation implements Calculation {
  private static Queue<MatrixMultiplyElementCalculation> POOL = new ArrayBlockingQueue<MatrixMultiplyElementCalculation>( Runtime.getRuntime().availableProcessors() );
  
  public Matrix myMatrix1;
  public Matrix myMatrix2;
  public Matrix myResultMatrix;
  public int myRow;
  public int myColumn;
  public double myResult;
  public CountDownLatch myLatch;
  
  public MatrixMultiplyElementCalculation( Matrix aMatrix1, Matrix aMatrix2, Matrix aResultMatrix, int aRow, int aColumn, CountDownLatch aLatch ) {
    super();
    myMatrix1 = aMatrix1;
    myMatrix2 = aMatrix2;
    myResultMatrix = aResultMatrix;
    myRow = aRow;
    myColumn = aColumn;
    myLatch = aLatch;
  }

  public void execute() {
    myResult = 0;
    for(int thePosition=0;thePosition<myMatrix1.getColumns();thePosition++){
      myResult += myMatrix1.getValueAt(myRow, thePosition) * myMatrix2.getValueAt(thePosition, myColumn);
    }
    myResultMatrix.setValueAt(myRow,myColumn, myResult);
    myLatch.countDown();
    freeInstance( this );
  }

  public void setRow( int aRow ) {
    myRow = aRow;
  }

  public void setColumn( int aColumn ) {
    myColumn = aColumn;
  }
  
  public static synchronized MatrixMultiplyElementCalculation getInstance(){
    return POOL.poll();
  }
  
  public static synchronized void freeInstance(MatrixMultiplyElementCalculation anInstance){
    POOL.add( anInstance );
  }
  
  public static synchronized void clear(){
    POOL.clear();
  }
}
