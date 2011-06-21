package chabernac.tools;

import junit.framework.TestCase;

public class SmileyToolsTest extends TestCase {
  public void testReplaceSmileys(){
    SmileyTools.ENABLED = true;
    assertEquals("http://serve.mysmiley.net/happy/happy0024.gif", SmileyTools.replaceSmileys(":-)"));
    assertEquals("http://serve.mysmiley.net/sad/sad0024.gif", SmileyTools.replaceSmileys(":-("));
    
    assertEquals("http://serve.mysmiley.net/sad/sad0024.gif", SmileyTools.replaceSmileys("[smiley:sad0024]"));
  }
}
