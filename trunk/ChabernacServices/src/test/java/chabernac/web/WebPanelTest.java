/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.web;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import chabernac.web.WebCamPanel;

public class WebPanelTest {

  /**
   * @param args
   */
  public static void main( String[] args ) {
    JFrame theFrame = new JFrame();
    theFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    theFrame.setSize( 640, 480);
    theFrame.setVisible( true );
    theFrame.getContentPane().setLayout( new BorderLayout() );
    WebCamPanel thePanel = new WebCamPanel();
    thePanel.setBorder( new TitledBorder( "test" ) );
    theFrame.getContentPane().add( thePanel, BorderLayout.NORTH );
    theFrame.getContentPane().add( new JLabel("test"), BorderLayout.SOUTH );
    theFrame.pack();
    thePanel.start();
  }

}
