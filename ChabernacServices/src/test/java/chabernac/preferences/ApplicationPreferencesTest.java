package chabernac.preferences;

import junit.framework.TestCase;
import chabernac.preference.ApplicationPreferences;

public class ApplicationPreferencesTest extends TestCase {
  public enum Test{A,B};
  public enum Test2{A,B};
  
  public void testApplicationPreferences(){
    ApplicationPreferences.getInstance().setEnumProperty(Test.A);
    assertTrue(ApplicationPreferences.getInstance().hasEnumProperty(Test.A));
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test.B));
    ApplicationPreferences.getInstance().setEnumProperty(Test.B);
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test.A));
    assertTrue(ApplicationPreferences.getInstance().hasEnumProperty(Test.B));
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test2.A));
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test2.B));
    
    assertTrue(ApplicationPreferences.getInstance().hasEnumType(Test.class));
    assertFalse(ApplicationPreferences.getInstance().hasEnumType(Test2.class));
    
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test2.A, Test2.B));
    assertTrue(ApplicationPreferences.getInstance().hasEnumProperty(Test2.B, Test2.B));
    ApplicationPreferences.getInstance().setEnumProperty(Test2.A);
    assertTrue(ApplicationPreferences.getInstance().hasEnumProperty(Test2.A, Test2.B));
    assertFalse(ApplicationPreferences.getInstance().hasEnumProperty(Test2.B, Test2.B));
    
  }
}
