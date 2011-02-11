/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.DocumentationBase;

public class DocumentationBasePanel extends JPanel {
  private static final long serialVersionUID = -2323738545251208902L;

  private final Artifact myArtifact;
  private final ArtifactBaseListModel myListModel;
  private JTextField myPrefixField = new JTextField();
  private JTextField myBaseField = new JTextField();

  private JList myBaseList;

  public DocumentationBasePanel( Artifact aArtifact ) {
    super();
    myArtifact = aArtifact;
    myListModel = new ArtifactBaseListModel( myArtifact );
    buildGUI();
  }

  private void buildGUI(){
    myPrefixField.setColumns( 5 );
    myBaseList = new JList(myListModel);
    setLayout( new BorderLayout() );
    add(new JScrollPane(myBaseList), BorderLayout.CENTER);

    JPanel theSouthPanel = new JPanel(new BorderLayout());
    theSouthPanel.add(myPrefixField, BorderLayout.WEST);
    theSouthPanel.add(myBaseField, BorderLayout.CENTER);

    JButton theBrowseButton = new JButton(new BrowseAction());
    JButton theRemoveButton = new JButton(new RemoveAction());
    JButton theAddButton = new JButton(new AddAction());

    JPanel theButtonPanel= new JPanel(new FlowLayout());
    theButtonPanel.add( theBrowseButton );
    theButtonPanel.add( theRemoveButton );
    theButtonPanel.add( theAddButton );


    theSouthPanel.add(theButtonPanel, BorderLayout.EAST);
    add(theSouthPanel, BorderLayout.SOUTH);
    //    setBorder( new TitledBorder( "Documentation base" ) );
  }


  public class BrowseAction extends AbstractAction{
    public BrowseAction(){
      putValue( Action.NAME, "Browse" );
    }

    @Override
    public void actionPerformed( ActionEvent aE ) {
      JFileChooser theFileChooser = new JFileChooser( new File(myBaseField.getText()) );
      theFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int theResult = theFileChooser.showOpenDialog( null );
      if(theResult == JFileChooser.APPROVE_OPTION){
        File theDir = theFileChooser.getSelectedFile();
        myArtifact.addDocumentationBase( new DocumentationBase( myPrefixField.getText(), theDir ) );
        myListModel.fireChanged();
      }
    }
  }

  public class AddAction extends AbstractAction{
    public AddAction(){
      putValue( Action.NAME, "Add" );
    }

    @Override
    public void actionPerformed( ActionEvent aE ) {
      File theFile = new File(myBaseField.getText());
      if(theFile.isDirectory()){
        myArtifact.addDocumentationBase( new DocumentationBase( myPrefixField.getText(), theFile ) );
        myListModel.fireChanged();
      }
    }
  }

  public class RemoveAction extends AbstractAction{
    public RemoveAction(){
      putValue( Action.NAME, "Remove" );
    }

    @Override
    public void actionPerformed( ActionEvent aE ) {
      DocumentationBase theBase = (DocumentationBase)myBaseList.getSelectedValue();
      myArtifact.removeDocumentationBase( theBase );
      myListModel.fireChanged();
    }
  }
}
