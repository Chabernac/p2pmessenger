package chabernac.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import chabernac.preference.ApplicationPreferences;
import chabernac.preference.iApplicationPreferenceListener;

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

  public void testApplicationPreferenceListener(){
    ApplicationPreferenceCollecter theCollector = new ApplicationPreferenceCollecter();
    ApplicationPreferences.getInstance().addApplicationPreferenceListener( theCollector );
    
    ApplicationPreferences.getInstance().setProperty( "a", "b" );
    ApplicationPreferences.getInstance().setEnumProperty( Test.A );
    
    assertEquals( "b",theCollector.getCollectedKeyValues().get( "a" ));
    assertTrue( theCollector.getCollectedEnums().contains( Test.A ) );

  }

  public class ApplicationPreferenceCollecter implements iApplicationPreferenceListener {
    private final Map<String, String> myCollectedKeyValues = new HashMap< String, String >();
    private final List< Enum > myCollectedEnums = new ArrayList< Enum >();

    @Override
    public void applicationPreferenceChanged( String aKey, String aValue ) {
      myCollectedKeyValues.put(aKey, aValue);
    }

    @Override
    public void applicationPreferenceChanged( Enum anEnumValue ) {
      myCollectedEnums.add( anEnumValue );
    }

    public Map< String, String > getCollectedKeyValues() {
      return myCollectedKeyValues;
    }

    public List< Enum > getCollectedEnums() {
      return myCollectedEnums;
    }
  }
}
