package chabernac.math;

public class Matrix{
  protected double[] myMatrix = null;
  private int myRows;
  private int myColumns;

  public Matrix(int aRows, int aColumns){
    this(aRows, aColumns, new double[aRows * aColumns]);
  }
  public Matrix(int aRows, int aColumns, double[] aMatrix){
    myRows = aRows;
    myColumns = aColumns;
    myMatrix = aMatrix;
  }

  public double[] getSource(){
    return myMatrix;
  }

  public int getRows(){
    return myRows;
  }

  public int getColumns(){
    return myColumns;
  }

  public void setSource(double[] aMatrix){
    myMatrix = aMatrix;
  }

  public double getValueAt(int aRow, int aColumn){
    return myMatrix[aRow * myColumns + aColumn];
  }

  public void setValueAt(int aRow, int aColumn, double aValue){
    myMatrix[aRow * myColumns + aColumn] = aValue;
  }

  public double getDeterminant() throws MatrixException{
    if(myRows != myColumns){
      throw new MatrixException("Could not calculate determinant");
    }
    //System.out.println("Calculating determinant of: " + toString());
    if(myRows == 1){
      return myMatrix[0];
    }
    double theValue = 0;
    for(int theColumn=0;theColumn<myColumns;theColumn++){
      theValue += Math.pow(-1, theColumn) * myMatrix[theColumn] * exclude(0,theColumn).getDeterminant();
    }
    //System.out.println("Value: " + theValue);
    return theValue;
  }

  /**
   * calculate the cross product of 2 matrices
   * @param aMatrix
   * @return
   * @throws MatrixException
   */
  //  public Matrix multiplyMultiThreaded(Matrix aMatrix) throws MatrixException{
  //    if(getColumns() != aMatrix.getRows()){
  //      throw new MatrixException("Could not multiply matrices [" + getRows() + "," + getColumns() + "] x [" + aMatrix.getRows() + "," + aMatrix.getColumns() + "]");
  //    }
  //    
  //    Matrix theMatrix = new Matrix(getRows(), aMatrix.getColumns());
  //    
  //    CountDownLatch theLatch = new CountDownLatch( getRows() * aMatrix.getColumns() );
  //    for(int i=0;i<myCalculator.getProcessors();i++){
  //      MatrixMultiplyElementCalculation.freeInstance( new MatrixMultiplyElementCalculation( this, aMatrix, theMatrix, 0, 0, theLatch ) );
  //    }
  //    
  //    for(int theMultiplyColumn=0;theMultiplyColumn<aMatrix.getColumns();theMultiplyColumn++){
  //      for(int theMultiplyRow=0;theMultiplyRow<getRows();theMultiplyRow++){
  //        MatrixMultiplyElementCalculation theCalc = MatrixMultiplyElementCalculation.getInstance();
  //        theCalc.setRow( theMultiplyRow );
  //        theCalc.setColumn( theMultiplyColumn );
  //        myCalculator.calculate( theCalc );
  //      }
  //    }
  //    
  //    try {
  //      theLatch.await();
  //    } catch ( InterruptedException e ) {
  //      throw new MatrixException( "Could not wait" );
  //    }
  //    
  //    MatrixMultiplyElementCalculation.clear();
  //    
  //    return theMatrix;
  //  }

  /**
   * calculate the cross product of 2 matrices
   * @param aMatrix
   * @return
   * @throws MatrixException
   */
  public Matrix multiply(Matrix aMatrix) throws MatrixException{
    if(myColumns != aMatrix.getRows()){
      throw new MatrixException("Could not multiply matrices [" + myRows + "," + myColumns + "] x [" + aMatrix.getRows() + "," + aMatrix.getColumns() + "]");
    }
    int theColumns = aMatrix.getColumns();
    Matrix theMatrix = new Matrix(myRows, theColumns);
    double theValue;
    for(int theMultiplyColumn=0;theMultiplyColumn<theColumns;theMultiplyColumn++){
      for(int theMultiplyRow=0;theMultiplyRow<myRows;theMultiplyRow++){
        theValue = 0;
        for(int thePosition=0;thePosition<myColumns;thePosition++){
          theValue += myMatrix[theMultiplyRow * myColumns + thePosition] * aMatrix.getValueAt(thePosition, theMultiplyColumn);
        }
        theMatrix.myMatrix[theMultiplyRow * theColumns + theMultiplyColumn] = theValue;
//        theMatrix.setValueAt(theMultiplyRow,theMultiplyColumn, theValue);
      }
    }
    return theMatrix;
  }

  public Matrix multiply2(Matrix aMatrix) throws MatrixException{
    if(getColumns() != aMatrix.getRows()){
      throw new MatrixException("Could not multiply matrices [" + getRows() + "," + getColumns() + "] x [" + aMatrix.getRows() + "," + aMatrix.getColumns() + "]");
    }
    
    int theColomsOfGivenMatrix = aMatrix.getColumns();
    
    Matrix theMatrix = new Matrix(myRows, theColomsOfGivenMatrix);
    
    //the value at row, column with which we will do all calculations at once
    double theTemp;
    
    //the index in the matrix of this object
    int theMeIndex = 0;
    
    //the index in the new matrix
    int theIndex;
    
    //the index in the given matrix
    int theOtherIndex;
    
    
    
    for(int theRow=0;theRow<myRows;theRow++){
      for(int theColumn=0;theColumn<myColumns;theColumn++){
        theTemp = myMatrix[theMeIndex++];
        theIndex = theRow * theColomsOfGivenMatrix;
        theOtherIndex = theColumn * theColomsOfGivenMatrix;
        for(int thePosition=0;thePosition<theColomsOfGivenMatrix;thePosition++){
          theMatrix.myMatrix[theIndex++] += theTemp * aMatrix.myMatrix[theOtherIndex++];
        }
      }
    }
    return theMatrix;
  }

  public Matrix fastMultiply(Matrix aMatrix) throws MatrixException{
    if(getColumns() != aMatrix.getRows()){
      throw new MatrixException("Could not multiply matrices [" + getRows() + "," + getColumns() + "] x [" + aMatrix.getRows() + "," + aMatrix.getColumns() + "]");
    }
    Matrix theMatrix = new Matrix(getRows(), aMatrix.getColumns());
    double theValue = 0;
    int theMultiplyColumn=0; 
    do{
      int theMultiplyRow=0;
      do{
        int thePosition=0;
        theValue = 0;
        do{
          theValue += getValueAt(theMultiplyRow, thePosition) * aMatrix.getValueAt(thePosition, theMultiplyColumn);
          thePosition++;
        }while(thePosition != getColumns());
        theMatrix.setValueAt(theMultiplyRow,theMultiplyColumn, theValue);
        theMultiplyRow++;
      }while(theMultiplyRow != getRows());
      theMultiplyColumn++;
    }while(theMultiplyColumn != aMatrix.getColumns());
    return theMatrix;
  }


  public Matrix multiply(double adouble){
    double[] theMatrix = new double[myMatrix.length];  	
    for(int i=0;i<myMatrix.length;i++){
      theMatrix[i] = myMatrix[i] * adouble; 
    }
    return new Matrix(getRows(), getColumns(), theMatrix);
  }

  public Matrix exp(int aExponent) throws MatrixException{
    if(getRows() != getColumns()){
      throw new MatrixException("Can not take exponent");
    }
    Matrix theMatrix = this;
    for(int i=1;i<aExponent;i++){
      theMatrix = theMatrix.multiply(this);
    }
    return theMatrix;
  }

  public Matrix exclude(int aRow, int aColumn) throws MatrixException{
    if(aRow >= myRows || aColumn >= myColumns || aRow < 0 || aColumn < 0){
      throw new MatrixException("Index out of bounds");
    }
    double theMatrix[] = new double[(myRows - 1)*(myColumns - 1)];
    int i=0;
    for(int theRow=0;theRow<myRows;theRow++){
      for(int theColumn=0;theColumn<myColumns;theColumn++){
        if(theRow!=aRow && theColumn!=aColumn){
          theMatrix[i++] = myMatrix[theRow * myColumns + theColumn];
        }
      }
    }
    return new Matrix(myRows - 1, myColumns - 1, theMatrix);
  }

  public Matrix transpose(){
    Matrix theMatrix = new Matrix(getColumns(),getRows());
    for(int theRow=0;theRow<getRows();theRow++){
      for(int theColumn=0;theColumn<getColumns();theColumn++){
        theMatrix.setValueAt(theColumn,theRow,getValueAt(theRow,theColumn));
      }
    }
    return theMatrix;
  }

  public Matrix inverse() throws MatrixException{
    double theDet = getDeterminant();
    if(theDet == 0) throw new MatrixException("This matrix has no inverse");
    if(isOrthogonal()) return transpose();
    Matrix theMatrix = new Matrix(getRows(),getColumns());
    for(int theColumn=0;theColumn<getColumns();theColumn++){
      for(int theRow=0;theRow<getRows();theRow++){
        theMatrix.setValueAt(theRow, theColumn, (double)Math.pow(-1,theRow + theColumn) * exclude(theRow, theColumn).getDeterminant() / theDet);
      }
    }
    return theMatrix.transpose();
  }

  public boolean isOrthogonal(){
    if(getRows() != getColumns()) return false;
    for(int theRow=0; theRow<getRows() - 1; theRow++){
      for(int theRow2=theRow + 1; theRow2<getRows(); theRow2++){
        double theValue = 0;
        for(int theColumn=0; theColumn<getColumns(); theColumn++){
          theValue += getValueAt(theRow, theColumn) * getValueAt(theRow2, theColumn);
        }        
        //Debug.log(this,"Value: " + theValue);
        if(theValue != 0)return false;
      }
    }
    return true;
  }

  public Matrix getRowAsMatrix(int aRow){
    Matrix theMatrix = new Matrix(1,myColumns);
    for(int theColumn=0; theColumn<getColumns(); theColumn++){
      theMatrix.setValueAt(0,theColumn, getValueAt(aRow, theColumn));
    }
    return theMatrix;
  }

  public Matrix add(Matrix aMatrix) throws MatrixException{
    if(getColumns() != aMatrix.getColumns() || getRows() != aMatrix.getRows()){
      throw new MatrixException("Can not add matrices");
    }
    double theMatrix[] = new double[myMatrix.length];
    double theSource[] = aMatrix.getSource();
    for(int i=0;i<myMatrix.length;i++){
      theMatrix[i] = myMatrix[i] + theSource[i];
    }
    return new Matrix(getRows(),getColumns(),theMatrix);
  }

  public String toString(){
    StringBuffer theBuffer = new StringBuffer();
    for(int theRow=0;theRow<getRows();theRow++){
      for(int theColumn=0;theColumn<getColumns();theColumn++){
        theBuffer.append(getValueAt(theRow,theColumn));  
        theBuffer.append(",");
      }
      theBuffer.append(";");
    }
    return theBuffer.toString();
  }

  public void print(){
    System.out.println();
    for(int row=0;row<getRows();row++){
      for(int column=0;column<getColumns();column++){
        System.out.print("[");
        System.out.print(getValueAt(row, column));
        System.out.print("\t]");
      }
      System.out.println();
    }
  }
}