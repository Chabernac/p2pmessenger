/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JProgressBar;

public class FileHandlerDialog extends JDialog implements iFileHandler{

  private static final long serialVersionUID = 3831590489960913889L;
  private JProgressBar myProgressBar = new JProgressBar();
  private File myCurrentFile = null;
  
  public FileHandlerDialog(){
    super((Frame)null, false);
    setupGUI();
  }
  
  private void setupGUI(){
    myProgressBar.setStringPainted( true );
    
    getContentPane().setLayout( new GridBagLayout() );
    
    GridBagConstraints theConstraints = new GridBagConstraints();
    theConstraints.insets = new Insets(5,5,5,5);
    theConstraints.gridx = 0;
    theConstraints.gridy = 0;
    theConstraints.fill = GridBagConstraints.BOTH;
    theConstraints.weightx = 1;
    theConstraints.weighty = 1;
    
    getContentPane().add( myProgressBar, theConstraints);
    
    pack();
    setSize( 400, 80);
  }

  @Override
  public File acceptFile( String aFileName ) {
    
    JFileChooser theFileChooser = new JFileChooser();
    theFileChooser.setSelectedFile( new File(aFileName) );
    int theReturn = theFileChooser.showOpenDialog( this );
    if(theReturn == JFileChooser.APPROVE_OPTION){
      myCurrentFile = theFileChooser.getSelectedFile();
      setVisible(true);
      setTitle( myCurrentFile.getAbsolutePath() );
      return myCurrentFile;
    }
    return null;
  }

  @Override
  public void fileSaved( File aFile ) {
    myProgressBar.setForeground( new Color(50,200,50));
//    myProgressBar.setString( "File saved" );
    
  }

  @Override
  public void fileTransfer( File aFile, long aBytesReceived, long aTotalBytes ) {
    String theFileName = aFile.getName();
    if(theFileName.length() > 20) theFileName = theFileName.substring( 0, 20 );
    myProgressBar.setString( "Transferring file '" + theFileName + "' "  + (aBytesReceived/1024) + "/" + (aTotalBytes/1024) + " Kbytes");
    myProgressBar.setMaximum( 100 );
    myProgressBar.setValue( (int)(aBytesReceived * 100 / aTotalBytes ));
    
  }

  @Override
  public void fileTransferInterrupted( File aFile ) {
    myProgressBar.setForeground( Color.red);
    myProgressBar.setString( "File transfer interrupted" );    
  }

  public static void main(String args[]) throws InterruptedException{
    FileHandlerDialog theDialog = new FileHandlerDialog();
    File theFile = theDialog.acceptFile( "test.txt" );
    if(theFile != null){
      theDialog.fileTransfer( theFile, 500, 1024 );
    }
    Thread.sleep( 5000 );
    theDialog.fileSaved( theFile );
    Thread.sleep( 2000 );
    theDialog.fileTransferInterrupted(  theFile );
  }
}
