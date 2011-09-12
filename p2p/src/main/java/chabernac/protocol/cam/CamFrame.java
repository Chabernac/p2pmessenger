/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class CamFrame extends JFrame {
  private static final long serialVersionUID = 7244851544793739106L;
  private BufferedImage myImage = null;
  
  private final P2PFacade myFacade;
  private String myUserId;
  
  private JRadioButton mySmall = new JRadioButton("Small");
  private JRadioButton myMedium = new JRadioButton("Medium");
  private JRadioButton myLarge = new JRadioButton("Large");
  private JSlider myQualitySlider = new JSlider(1, 100, 60);
  
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
  }
  
  private void addCammListener(){
    try {
      myFacade.setCamListener( myCamPanel );
    } catch ( P2PFacadeException e ) {
    }
  }
  
  private void buildGUI(){
    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add( buildControlPanel(), BorderLayout.NORTH );
    myQualitySlider.setMinorTickSpacing( 5 );
    getContentPane().add( myCamPanel, BorderLayout.CENTER );
  }

  private Component buildControlPanel() {
    JPanel theControlPanel = new JPanel(new GridBagLayout());
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.weightx = 0;
    theCons.weighty = 0;
    theCons.gridx = 0;
    theCons.gridy = 0;
    theControlPanel.add( mySmall );
    theCons.gridx++;
    theControlPanel.add( myMedium );
    theCons.gridx++;
    theControlPanel.add( myLarge );
    
    theCons.weightx = 1;
    theCons.fill = GridBagConstraints.HORIZONTAL;
    theCons.gridx++;
    theControlPanel.add(myQualitySlider);
    
    theCons.weightx = 0;
    theCons.gridx++;
    theControlPanel.add(new CommandButton( new SnapShotCommand() ));
    
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
      }
    }
  }

}
