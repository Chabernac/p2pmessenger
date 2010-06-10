/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class DialogAlwaysOnTopTest {
  public static void main(String args[]) throws InterruptedException{
//    Thread.sleep( 5000 );
    JDialog theDialog = new JDialog((JFrame)null, true);
    theDialog.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
    theDialog.setAlwaysOnTop( true );
    theDialog.setTitle( "test" );
    theDialog.setSize( 200,100 );
    theDialog.setVisible( true );
    
   
  }

}
