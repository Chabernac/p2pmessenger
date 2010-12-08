/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.math;

public class Matrix2 extends AbstractMatrix {
  
  private double[][] myMatrix;

  public Matrix2( int aRows, int aColumns ) {
    super( aRows, aColumns );
    myMatrix = new double[myRows][myColumns];
  }
  
  @Override
  public AbstractMatrix add( AbstractMatrix aMatrix ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getValueAt( int aRow, int aColumn ) {
    return myMatrix[aRow][aColumn];
  }

  @Override
  public AbstractMatrix multiply(AbstractMatrix aMatrix) throws MatrixException{
    if(getColumns() != aMatrix.getRows()){
      throw new MatrixException("Could not multiply matrices [" + getRows() + "," + getColumns() + "] x [" + aMatrix.getRows() + "," + aMatrix.getColumns() + "]");
    }
    Matrix2 theMatrix = new Matrix2(getRows(), aMatrix.getColumns());
    double theValue = 0;
    for(int theMultiplyColumn=0;theMultiplyColumn<aMatrix.getColumns();theMultiplyColumn++){
      for(int theMultiplyRow=0;theMultiplyRow<getRows();theMultiplyRow++){
        theValue = 0;
        for(int thePosition=0;thePosition<getColumns();thePosition++){
          theValue += getValueAt(theMultiplyRow, thePosition) * aMatrix.getValueAt(thePosition, theMultiplyColumn);
        }
        theMatrix.setValueAt(theMultiplyRow,theMultiplyColumn, theValue);
      }
    }
    return theMatrix;
  }

  @Override
  public void setValueAt( int aRow, int aColumn, double aValue ) {
    myMatrix[aRow][aColumn ] = aValue;
  }

  @Override
  public AbstractMatrix subtract( AbstractMatrix aMatrix ) {
    // TODO Auto-generated method stub
    return null;
  }
}
