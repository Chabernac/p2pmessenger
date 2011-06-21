/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkReplacer {
  private enum Mode{TEXT, LINK, IMG};
  private final static String[] IMAGE_EXTENTIONS = new String[]{"jpg","gif","png","bmp","tiff"}; 

  private final String myText;
  private Mode myMode = Mode.TEXT;
  
  private final Pattern URL_PATTERN = Pattern.compile( "(?i)(.*://[^<>\\s]+[\\p{Alnum}/]+.)");
  public final static int MAX_URL_LENGTH = 30;

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
      else if(theToken.equalsIgnoreCase("<img")) myMode = Mode.IMG;

      if(myMode == Mode.TEXT){
        String theNewToken = theToken;
        for(String theExtention : IMAGE_EXTENTIONS){
          if(theNewToken.length() == theToken.length()) theNewToken = theToken.replaceAll("(?i)(.*://[^<>\\s]+[\\p{Alnum}/]+." + theExtention + ")", "<img src=\"$1\">");
        }
        
        if(theNewToken.length() == theToken.length()) theNewToken = replaceLinkInString( theToken );
        //if(theNewToken.length() == theToken.length()) theNewToken = theToken.replaceAll("(.*://[^<>\\s]+[\\p{Alnum}/]+)", "<a href=\"$1\">$1</a>"); 
        theBuilder.append(theNewToken);
      } else {
        theBuilder.append(theToken);
      }

      if(myMode == Mode.IMG){
        if(theToken.endsWith(">") || theToken.endsWith("/>")){
          myMode = Mode.TEXT;
        }
      }

      if(theTokenizer.hasMoreTokens()) theBuilder.append(" ");
    }
    return theBuilder.toString();
  }
  
  private String replaceLinkInString(String aString){
    Matcher theMatcher = URL_PATTERN.matcher( aString );
    
    String theResult = aString;
    
    while(theMatcher.find()){
      String theURL = theMatcher.group( 1 );
      String theContent = theURL;
      if(theContent.length() > MAX_URL_LENGTH){
        theContent = theContent.substring( 0, MAX_URL_LENGTH ) + "...";
      }
      theResult = theResult.replace( theURL, "<a href=\"" + theURL + "\">" + theContent + "</a>" );
    }
    return theResult;
  }

}
