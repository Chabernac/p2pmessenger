/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.web;


public class WebCamPanel extends JVideoScreen {
  private static final long serialVersionUID = -7757405759734484204L;
  private final WebCamDecorator myWebCamDecorator;
  
  public WebCamPanel(){
    super(true);
    myWebCamDecorator = new WebCamDecorator( this );
  }
  
  public void start(){
    myWebCamDecorator.start();
  }
}
