/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JCheckBox;

import chabernac.image.ImageFactory;
import chabernac.paint.Selector;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.userinfo.UserInfo.Status;

public class StatusCheckBox extends JCheckBox {
  private static final long serialVersionUID = 7265127493485601206L;
//  private static Logger logger = Logger.getLogger(StatusCheckBox.class);
  private Status myStatus = Status.ONLINE;
  private static final Insets STATUS_MARGIN = new Insets(0,18,0,0);
  private static final Insets DEFAULT_MARGIN = new Insets(0,0,0,0);
  
  public StatusCheckBox(){
    this(Status.ONLINE);
  }
  
  public StatusCheckBox(Status aStatus){
    super();
    myStatus = aStatus;
    //myMargin = getMargin();
    setMargin(DEFAULT_MARGIN);
    setFont();
  }
  
  private void setFont(){
    String thefontSize = ApplicationPreferences.getInstance().getProperty("userlist.font.size");
    //String theFont = ApplicationPreferences.getInstance().getProperty("userlist.font.name");
    //String theFontStyle = ApplicationPreferences.getInstance().getProperty("userlist.font.style");
    //if(thefontSize != null && theFontStyle != null) setFont(getFont().deriveFont(theFontStyle, thefontSize));
    if(thefontSize != null) setFont(getFont().deriveFont(Float.parseFloat(thefontSize)));
  }
  
  public void setFontSize(int aSize){
    setFont(getFont().deriveFont((float)aSize));
  }
  
  
  public void setStatus(Status aStatus){
    myStatus = aStatus;
    if(drawImage()){
      setMargin(STATUS_MARGIN);
    } else  {
      setMargin(DEFAULT_MARGIN);
    }
    repaint();
  }
  
  public Status getStatus(){
    return myStatus;
  }
  
  public boolean drawImage(){
    if(getStatus() == Status.AWAY || getStatus() == Status.BUSY) return true;
    return false;
  }
  
  public void paint(Graphics g){
    super.paint(g);
    
    if(drawImage()){
      Image theImage = ImageFactory.loadImage("status_" + myStatus, true);
      if(theImage != null){
        g.drawImage(theImage, 3, (getHeight() / 2) - (theImage.getHeight(null) / 2), null);
      }
      
      g.setColor(Color.gray);
      Selector theSelector = new Selector(new Rectangle(2,2,getWidth() - 4, getHeight() - 4), 3);
      theSelector.setHalf(true);
      theSelector.paint(g);
    }
  }
}
