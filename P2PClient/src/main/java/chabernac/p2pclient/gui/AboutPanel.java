/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AboutPanel extends JPanel{
  private static final long serialVersionUID = 1497984488242052479L;

  public AboutPanel(){
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    try {
      JEditorPane thePane = new JEditorPane("http://www.opendesigns.org/od/wp-content/designs/2/2127/?KeepThis=true");
      add(BorderLayout.CENTER, new JScrollPane( thePane ));
    } catch ( IOException e ) {
    }
  }
}
