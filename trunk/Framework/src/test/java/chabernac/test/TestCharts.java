/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import chabernac.statistics.Data;
import chabernac.statistics.LineChart;

/**
 *
 *
 * @version v1.0.0      Sep 26, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 26, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class TestCharts extends JFrame{
  
  public TestCharts(){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    buildGUI();
  }
  
  private void buildGUI(){
    LineChart theChart = new LineChart();
    theChart.setXLabel("Date");
    theChart.setYLabel("Hours");
    theChart.setBackground(new Color(245,245,255));
    
    Data theData = new Data("test");
    theData.addValue(-2,4);
    theData.addValue(2,7);
    theData.addValue(3,1);
    theData.addValue(4,2);
    theData.addValue(9,-2);
    theChart.addData(theData);
    
    theData = new Data("test");
    theData.addValue(-4,10);
    theData.addValue(3,5);
    theData.addValue(5,1);
    theData.addValue(2,12);
    theData.addValue(8,-1);
    theChart.addData(theData);
    
    theData = new Data("test");
    theData.addValue(5,9);
    theData.addValue(2,15);
    theData.addValue(11,5);
    theData.addValue(6,3);
    theData.addValue(1,7);
    theChart.addData(theData);
    
    theData = new Data("test");
    theData.addValue(8,2);
    theData.addValue(1,6);
    theData.addValue(14,8);
    theData.addValue(17,2);
    theData.addValue(3,7);
    theChart.addData(theData);
    
    theData = new Data("test");
    theData.addValue(4,2);
    theData.addValue(1,6);
    theData.addValue(9,8);
    theData.addValue(7,2);
    theData.addValue(2,7);
    theChart.addData(theData);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(theChart, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame theFrame = new TestCharts();
    theFrame.setSize(300,300);
    theFrame.setVisible(true);
  }
}
