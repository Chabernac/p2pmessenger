/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import chabernac.documentationtool.document.Artifact;

public class ArtifactPanel extends JPanel {
  private final Artifact myArtifact;
  private final DocumentationToolMediator myMediator;
  private JSplitPane mySplitPane;

  public ArtifactPanel( DocumentationToolMediator aMediator, Artifact aArtifact ) {
    super();
    myArtifact = aArtifact;
    myMediator = aMediator;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    
    mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    mySplitPane.setTopComponent( builtTop() );
    mySplitPane.setBottomComponent( buildBottom() );
    mySplitPane.setDividerSize( 4 );
    mySplitPane.setResizeWeight( 1 );
    add(mySplitPane, BorderLayout.CENTER);
  }
  
  private JPanel builtTop(){
    JPanel thePanel = new JPanel(new BorderLayout(2,2));
    JScrollPane theDocBasePanel = new JScrollPane(new DocumentationBaseTextArea( myArtifact ), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    theDocBasePanel.setBorder( new TitledBorder( "Documentation base" ));
    thePanel.add(theDocBasePanel, BorderLayout.NORTH);
    thePanel.add(new DocumentationArea( myMediator, myArtifact ), BorderLayout.CENTER);
    return thePanel;
  }
  
  private JComponent buildBottom(){
    JTabbedPane thePane = new JTabbedPane();
    thePane.add("Attribute", new ArtifactAttributePanel( myArtifact ));
    return thePane;
  }
  
  private JPanel addInsets(JPanel aPanel){
    JPanel thePanel= new JPanel(new BorderLayout(20,20));
    thePanel.add(aPanel, BorderLayout.CENTER);
    return thePanel;
  }
  
  public void setDividerLocation(){
    mySplitPane.setDividerLocation( getHeight() - 150 );    
  }
  
  public Artifact getArtifact(){
    return myArtifact;
  }
}
