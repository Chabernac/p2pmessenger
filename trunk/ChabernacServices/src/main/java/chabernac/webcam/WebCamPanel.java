/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.webcam;

import java.awt.Frame;

public class WebCamPanel extends JVideoScreen {
  private static final long serialVersionUID = -7757405759734484204L;
  private final WebCamDecorator myWebCamDecorator;
  
  public WebCamPanel(Frame aParentFrame){
    super(true);
    myWebCamDecorator = new WebCamDecorator( this, aParentFrame );
  }
  
  public void start(){
    myWebCamDecorator.start();
  }
}
