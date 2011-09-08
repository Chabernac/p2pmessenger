/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.webcam;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.border.TitledBorder;

public class WebCamPanelTest {

  /**
   * @param args
   */
  public static void main( String[] args ) {
    JFrame theFrame = new JFrame();
    theFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    theFrame.setSize( 30, 30);
    theFrame.setVisible( true );
    theFrame.getContentPane().setLayout( new BorderLayout() );
    WebCamPanel thePanel = new WebCamPanel(theFrame);
    thePanel.setBorder( new TitledBorder( "test" ) );
    theFrame.getContentPane().add( thePanel, BorderLayout.NORTH );
    theFrame.pack();
    thePanel.start();
  }

}
