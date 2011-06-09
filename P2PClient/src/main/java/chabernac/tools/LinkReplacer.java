/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.util.StringTokenizer;

public class LinkReplacer {
  private enum Mode{TEXT, LINK};
  
  private final String myText;
  private Mode myMode = Mode.TEXT;

  public LinkReplacer( String aText ) {
    super();
    myText = aText;
  }
  
  public String replace(){
    StringBuilder theBuilder = new StringBuilder();
    StringTokenizer theTokenizer = new StringTokenizer(myText, " ");
    while(theTokenizer.hasMoreElements()){
      String theToken = theTokenizer.nextToken();
      if(theToken.equalsIgnoreCase("<a")) myMode = Mode.LINK;
      else if(theToken.equalsIgnoreCase("</a>")) myMode = Mode.TEXT;
      
      if(myMode == Mode.LINK){
        theBuilder.append(theToken);
      } else {
        theBuilder.append(theToken.replaceAll("(.*://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>"));
      }
      if(theTokenizer.hasMoreTokens()) theBuilder.append(" ");
    }
    return theBuilder.toString();
  }

}
