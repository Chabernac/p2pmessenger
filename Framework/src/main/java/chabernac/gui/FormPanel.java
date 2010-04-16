/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 *
 * @version v1.0.0      Oct 24, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Oct 24, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class FormPanel extends JPanel {
  private Vector myPairs = new Vector();
  private boolean layoutDone = false;
    
  public FormPanel(){
    
  }
  
  public void addField(JLabel aLabel, Component anInputComponent){
    myPairs.add(new Object[]{aLabel, anInputComponent});
  }
  
  public void done(){
    /*
    setLayout(new SpringLayout());
    for(int i=0;i<myPairs.size();i++){
      Object[] thePair = (Object[])myPairs.elementAt(i);
      JLabel theLabel = new JLabel((String)thePair[0], JLabel.TRAILING);
      Component theComponent = (Component)thePair[1];
      theLabel.setLabelFor(theComponent);
      add(theLabel);
      add(theComponent);
    }
    
    SpringUtilities.makeCompactGrid(this, myPairs.size(), 2, 5, 5, 5, 5);
    */
    setLayout(new GridBagLayout());
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.insets = new Insets(1,2,1,2);
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.weightx = 1;
    theCons.weighty = 1;
    theCons.fill = GridBagConstraints.NONE;
    for(int i=0;i<myPairs.size();i++){
      Object[] thePair = (Object[])myPairs.elementAt(i);
      JLabel theLabel = (JLabel)thePair[0];
      Component theComponent = (Component)thePair[1];
      theLabel.setLabelFor(theComponent);
      theCons.gridy = i;
      theCons.gridx = 0;
      theCons.anchor = GridBagConstraints.EAST;
      add(theLabel, theCons);
      theCons.gridx = 1;
      theCons.anchor = GridBagConstraints.WEST;
      add(theComponent, theCons);
    }
  }
  
  public static void main(String args[]){
    JFrame theFrame = new JFrame();
    FormPanel thePanel = new FormPanel();
    for(int i=0;i<10;i++){
      thePanel.addField(new JLabel("veld " + (Math.pow(10,i)) + ":"), new JTextField(i*2));
    }
    thePanel.done();
    theFrame.getContentPane().setLayout(new BorderLayout());
    theFrame.getContentPane().add(BorderLayout.NORTH, thePanel);
    theFrame.pack();
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //theFrame.setSize(200,200);
    theFrame.setVisible(true);
  }

}
