/**
 * Copyright (c) 2009 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.task.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import chabernac.task.Period;
import chabernac.task.Task;

public class PeriodTableRenderer extends DefaultTableCellRenderer{


  public Component getTableCellRendererComponent  (JTable table, Object value, boolean isSelected,  boolean hasFocus, int row, int column) {
    PeriodTableModel theModel = (PeriodTableModel)table.getModel();

    Period thePeriod = (Period)theModel.getPeriods().get(row);

    Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(isSelected){
      cell.setBackground( Color.lightGray );
    } else {
      if(thePeriod.getEndTime() == -1){
        cell.setBackground( Color.green);
      } else if(thePeriod.spansMidnigth()){
        cell.setBackground( Color.red );
      } else if( thePeriod.isManuallyModified() ){
        cell.setBackground( Color.orange );
      } else {
        cell.setBackground( Color.white );
      }
     
      Task theTask = thePeriod.getTask();
      if(!theTask.hasAugeoCode() && theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_OWN_AUGEO_CODE){
        cell.setForeground( Color.blue );
      } else {
        cell.setForeground( Color.black );
      }
     
    }
    return cell;
  }

}
