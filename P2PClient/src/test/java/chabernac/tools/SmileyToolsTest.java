package chabernac.tools;

import junit.framework.TestCase;

public class SmileyToolsTest extends TestCase {
  public void testReplaceSmileys(){
    SmileyTools.ENABLED = true;
//    assertEquals("http://serve.mysmiley.net/happy/happy0024.gif", SmileyTools.replaceSmileys(":-)"));
//    assertEquals("http://serve.mysmiley.net/sad/sad0024.gif", SmileyTools.replaceSmileys(":-("));
    assertEquals("http://www.freewebthings.net/smileys/images/laughing/laughing-smiley-005.gif", SmileyTools.replaceSmileys(":-)"));
  assertEquals("http://www.freewebthings.net/smileys/images/sad/sad-smiley-052.gif", SmileyTools.replaceSmileys(":-("));
  assertEquals("http://www.freewebthings.net/smileys/images/other/scared.gif", SmileyTools.replaceSmileys(":-|"));
    
//    assertEquals("http://serve.mysmiley.net/sad/sad0024.gif", SmileyTools.replaceSmileys("[smiley:sad0024]"));
  assertEquals("http://www.freewebthings.net/smileys/images/sad/sad-smiley-024.gif", SmileyTools.replaceSmileys("[smiley:sad024]"));
  }
}
