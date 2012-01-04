package chabernac.utils;

import java.util.ArrayList;

import junit.framework.TestCase;

public class LimitedListDecoratorTest extends TestCase {
  public void testLimitedListDecorator(){
    LimitedListDecorator<String> theDecorator = new LimitedListDecorator<String>(3, new ArrayList<String>());
    
    for(int i=0;i<5;i++){
      theDecorator.add(Integer.toString(i));
    }
    
    assertFalse(theDecorator.contains("0"));
    assertFalse(theDecorator.contains("1"));
    assertTrue(theDecorator.contains("2"));
    assertTrue(theDecorator.contains("3"));
    assertTrue(theDecorator.contains("4"));
    
  }
}
