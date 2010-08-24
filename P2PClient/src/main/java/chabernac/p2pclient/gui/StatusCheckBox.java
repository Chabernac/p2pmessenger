/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JCheckBox;

import chabernac.image.ImageFactory;
import chabernac.paint.Selector;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.userinfo.UserInfo.Status;

public class StatusCheckBox extends JCheckBox {
  private static final long serialVersionUID = 7265127493485601206L;
//  private static Logger logger = Logger.getLogger(StatusCheckBox.class);
  private Status myStatus = Status.ONLINE;
  private static int LIGHT_WIDTH = 4;
  private static final Insets STATUS_MARGIN = new Insets(0,18,0,LIGHT_WIDTH + 2);
  private static final Insets DEFAULT_MARGIN = new Insets(0,0,0,LIGHT_WIDTH);
  
  private DeliveryReport.Status myDeliveryStatus = null;

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
    setEnabled( myStatus != Status.OFFLINE );
    if(myStatus == Status.OFFLINE){
      setSelected( false );
    }
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
  
  public DeliveryReport.Status getDeliveryStatus() {
    return myDeliveryStatus;
  }

  public void setDeliveryStatus( DeliveryReport.Status anDeliveryStatus ) {
    myDeliveryStatus = anDeliveryStatus;
    repaint();
  }

  public boolean drawImage(){
    if(getStatus() == Status.AWAY || getStatus() == Status.BUSY) return true;
    return false;
  }

  public void paint(Graphics g){
    super.paint(g);
    
    Graphics2D theG = (Graphics2D)g;
    theG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 

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
    
    if(myDeliveryStatus != null){
      Dimension theSize = getSize();
      int theX = theSize.width - LIGHT_WIDTH / 2 - 2;
      int theY = theSize.height / 4;
      if(myDeliveryStatus == DeliveryReport.Status.DELIVERED){
        theY *= 3;
        g.setColor( new Color(50,200,50) );
      } else if(myDeliveryStatus == DeliveryReport.Status.IN_PROGRESS){
        theY *= 2;
        g.setColor( Color.orange );
      } else if(myDeliveryStatus == DeliveryReport.Status.FAILED){
        theY *= 1;
        g.setColor( new Color(200,0,0) );
      }
      g.fillOval( theX - LIGHT_WIDTH / 2, theY - LIGHT_WIDTH / 2, LIGHT_WIDTH, LIGHT_WIDTH );
    }
  }
}
