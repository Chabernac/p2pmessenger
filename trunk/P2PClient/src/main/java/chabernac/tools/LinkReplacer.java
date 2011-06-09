/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

public class LinkReplacer {
  private enum Mode{TEXT, TAG};
  
  private final String myText;
  private Mode myMode;

  public LinkReplacer( String aText ) {
    super();
    myText = aText;
  }
  
  public void replace(){
    StringBuilder theBuilder = new StringBuilder();
  }

}
