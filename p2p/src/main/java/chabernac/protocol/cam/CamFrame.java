/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class CamFrame extends JFrame {
  private static final Logger LOGGER = Logger.getLogger(CamFrame.class);
  private static final long serialVersionUID = 7244851544793739106L;
  private static NumberFormat FORMAT = NumberFormat.getInstance();
  
  static{
    FORMAT.setMinimumFractionDigits(2);
    FORMAT.setMaximumFractionDigits(2);
  }
  
  private BufferedImage myImage = null;
  
  private final P2PFacade myFacade;
  private String myUserId;
  
  private JRadioButton mySmall = new JRadioButton("Small");
  private JRadioButton myMedium = new JRadioButton("Medium");
  private JRadioButton myLarge = new JRadioButton("Large");
  private JSlider myQualitySlider = new JSlider(1, 100, 60);
  private JTextField myQualityIndicator = new JTextField();
  
  private CamPanel myCamPanel = new CamPanel();

  public CamFrame( P2PFacade aFacade ) throws HeadlessException {
    super();
    init();
    myFacade = aFacade;
    buildGUI();
    addCammListener();
  }
  
  private void init(){
   ButtonGroup theGroup = new ButtonGroup();
   theGroup.add(mySmall);
   theGroup.add(myMedium);
   theGroup.add(myLarge);
   mySmall.setSelected(true);
   myQualityIndicator.setEnabled(false);
   myQualityIndicator.setColumns(3);
   myQualitySlider.addChangeListener(new QualityChangeListener());
   myQualitySlider.setPreferredSize(new Dimension(10, myQualityIndicator.getPreferredSize().height));
   changeQualityIndicator();
  }
  
  private void changeQualityIndicator(){
    myQualityIndicator.setText( FORMAT.format(((float)myQualitySlider.getValue()) / 100f ));
  }
  
  private void addCammListener(){
    try {
      myFacade.setCamListener( myCamPanel );
    } catch ( P2PFacadeException e ) {
      LOGGER.error("An error occured while setting cam listener", e);
    }
  }
  
  private void buildGUI(){
    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add( buildControlPanel(), BorderLayout.NORTH );
    myQualitySlider.setMinorTickSpacing( 5 );
    getContentPane().add( myCamPanel, BorderLayout.CENTER );
    setSize(400, 300);
  }

  private Component buildControlPanel() {
    JPanel theControlPanel = new JPanel(new GridBagLayout());
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.insets = new Insets(2, 2, 2, 2);
    theCons.weightx = 0;
    theCons.weighty = 0;
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.fill = GridBagConstraints.NONE;
    theControlPanel.add( mySmall, theCons );
    theCons.gridx++;
    theControlPanel.add( myMedium, theCons );
    theCons.gridx++;
    theControlPanel.add( myLarge, theCons );
    
    theCons.weightx = 1;
    theCons.fill = GridBagConstraints.HORIZONTAL;
    theCons.gridx++;
    theControlPanel.add(myQualitySlider, theCons);
    
    theCons.weightx = 0;
    theCons.fill = GridBagConstraints.NONE;
    theCons.gridx++;
    theControlPanel.add(myQualityIndicator, theCons);
    
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.gridy++;
    theControlPanel.add(new CommandButton( new SnapShotCommand() ), theCons);
    theCons.gridx++;
    theControlPanel.add(new CommandButton( new SaveCommand() ), theCons);
    
    return theControlPanel;
  }
  
  private int getSnapWidth(){
    if(mySmall.isSelected()) return 320;
    if(myMedium.isSelected()) return 640;
    if(myLarge.isSelected()) return 1024;
    return 320;
  }
  
  private int getSnapHeight(){
    if(mySmall.isSelected()) return 240;
    if(myMedium.isSelected()) return 480;
    if(myLarge.isSelected()) return 860;
    return 240;
  }
  
  public String getUserId() {
    return myUserId;
  }

  public void setUserId( String aUserId ) {
    myUserId = aUserId;
  }
  
  private class SnapShotCommand extends AbstractCommand {

    @Override
    public String getName() {
      return "Snapshot";
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void execute() {
      try {
        
        myFacade.requestCapture( myUserId, getSnapWidth(), getSnapHeight(), ((float)myQualitySlider.getValue()) / 100f );
      } catch ( P2PFacadeException e ) {
        LOGGER.error("an error occured while requesting capture", e);
      }
    }
  }
  private class QualityChangeListener implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent anArg0) {
     changeQualityIndicator();
    }
  }
  
  private class SaveCommand extends AbstractCommand {
    @Override
    public String getName() {
      return "Save";
    }

    @Override
    public boolean isEnabled() {
      return true; 
    }

    @Override
    public void execute() {
        BufferedImage theImage = myCamPanel.getImage();
        if(theImage == null) return;
        JFileChooser theChooser = new JFileChooser();
        int theResult = theChooser.showSaveDialog( CamFrame.this );
        if(theResult == JFileChooser.APPROVE_OPTION){
          try {
            ImageIO.write( theImage, "jpg", theChooser.getSelectedFile() );
          } catch ( IOException e ) {
            LOGGER.error("Could not write image", e);
          }
        }
    }
  }
}
