/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.task.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import chabernac.task.Task;

public class TaskTreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

  private static final Color BG_COLOR_FOCUS = new Color(200,200,250);
  private static final Color BG_COLOR_SELECTED = new Color(250,200,200);
  private static final Color BG_COLOR_RUNNING = new Color(200,250,200);
  
  @Override
  public Component getTreeCellRendererComponent( JTree aTree, Object aValue, boolean aSel, boolean aExpanded, boolean aLeaf, int aRow, boolean aHasFocus ) {
    
    Task theTask = (Task)aValue;
    JLabel theLabel = new JLabel(theTask.getName());

    if(theTask.isRunning()){
      theLabel.setOpaque( true );
      theLabel.setBackground( BG_COLOR_RUNNING );
    }
    
    if(aSel || aHasFocus){
      theLabel.setOpaque( true );
      theLabel.setBackground( BG_COLOR_FOCUS );
    }
    
    
    
    
    
    Font theFont = theLabel.getFont();
    theFont = theFont.deriveFont( 12 );
    Map  attributes = theFont.getAttributes();
    if(theTask.isCompleted()){
      attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
    } else {
      attributes.remove( TextAttribute.STRIKETHROUGH );
    }
    theLabel.setFont( theFont.deriveFont( attributes ));
    
    return theLabel;
  }

}
