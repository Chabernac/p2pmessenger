/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import junit.framework.TestCase;

public class HTMLToolsTest extends TestCase{
  public void testReplaceLinks(){
    assertEquals( "<img src=\"http://www.google.com/b.jpg\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.jpg" ) );
    assertEquals( "<img src=\"http://www.google.com/b.JPG\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.JPG" ) );
    assertEquals( "<img src=\"http://www.google.com/b.gif\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.gif" ) );
    assertEquals( "<img src=\"http://www.google.com/b.png\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.png" ) );
    assertEquals( "<img src=\"http://www.google.com/b.bmp\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.bmp" ) );
    assertEquals( "<img src=\"http://www.google.com/b.tiff\">", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/b.tiff" ) );
    assertEquals( "<a href=\"http://www.google.com\">http://www.google.com</a>", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com" ) );
    assertEquals( "<a href=\"http://www.google.com/a.html\">http://www.google.com/a.html</a>", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/a.html" ) );
    assertEquals( "<a href=\"http://www.google.com\">http://www.google.com</a>", HTMLTools.detectLinksInTextAndMakeHref( "<a href=\"http://www.google.com\">http://www.google.com</a>" ) );
    assertEquals( "<img src=\"http:www.img.com/a.jpg\">", HTMLTools.detectLinksInTextAndMakeHref( "<img src=\"http:www.img.com/a.jpg\">" ) );
    assertEquals( "<a href=\"http://www.google.com/search?q=thequickbrownfoxjumpseasilyoverthefatandlazydog\">http://www.google.com/search?q...</a>", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com/search?q=thequickbrownfoxjumpseasilyoverthefatandlazydog" ) );
  }
}
