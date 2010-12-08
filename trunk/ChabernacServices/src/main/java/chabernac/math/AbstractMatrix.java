/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.math;


public abstract class AbstractMatrix {
  protected int myRows;
  protected int myColumns;
  
  public AbstractMatrix(int aRows, int aColumns){
    myRows = aRows;
    myColumns = aColumns;
  }
  
  public abstract void setValueAt(int aRow, int aColumn, double aValue);
  public abstract double getValueAt(int aRow, int aColumn);
  public abstract AbstractMatrix multiply(AbstractMatrix aMatrix);
  public abstract AbstractMatrix add(AbstractMatrix aMatrix);
  public abstract AbstractMatrix subtract(AbstractMatrix aMatrix);
  
  public final int getRows(){
    return myRows;
  }
  
  public final int getColumns(){
    return myColumns;
  }
}
