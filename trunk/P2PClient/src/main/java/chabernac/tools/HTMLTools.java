/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

public class HTMLTools {
  
  /**
   * je kan zoeken met http://www.google.com
   * 
   * ==>
   * 
   * ja kan zoeken met <a href="http://www.google.com">http://www.google.com</a>
   * 
   * @param aText
   * @return
   */
  public static String detectLinksInTextAndMakeHref(String aText){
    return aText.replaceAll("([^\".]*://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
  }
}
