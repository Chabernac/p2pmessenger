/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import junit.framework.TestCase;

public class HTMLToolsTest extends TestCase{
  public void testReplaceLinks(){
    assertEquals( "<a href=\"http://www.google.com\">http://www.google.com</a>", HTMLTools.detectLinksInTextAndMakeHref( "http://www.google.com" ) );
    assertEquals( "<a href=\"http://www.google.com\">http://www.google.com</a>", HTMLTools.detectLinksInTextAndMakeHref( "<a href=\"http://www.google.com\">http://www.google.com</a>" ) );
  }
}
