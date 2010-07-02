package chabernac.protocol.version;

import junit.framework.TestCase;
import chabernac.version.Version;

public class VersionTest extends TestCase {
  public void testVersion(){
    Version theVersion = new Version("1.2.3");
    assertEquals( "1.2.3",  theVersion.toString() );
    
    Version theVersion2 = new Version("1.2.4");
    assertEquals( -1, theVersion.compareTo( theVersion2 ) );
    assertEquals( 1, theVersion2.compareTo( theVersion ) );
    
    theVersion = new Version("1.2.3");
    assertEquals( "1.2.3",  theVersion.toString() );
    
    theVersion2 = new Version("1.3.3");
    assertEquals( -1, theVersion.compareTo( theVersion2 ) );
    assertEquals( 1, theVersion2.compareTo( theVersion ) );
    
    theVersion = new Version("1.2.3");
    assertEquals( "1.2.3",  theVersion.toString() );
    
    theVersion2 = new Version("2.2.3");
    assertEquals( -1, theVersion.compareTo( theVersion2 ) );
    assertEquals( 1, theVersion2.compareTo( theVersion ) );
    
    theVersion2 = new Version("1.2.3");
    
    assertEquals( 0, theVersion2.compareTo( theVersion ) );
    
    theVersion = new Version(5,6,7);
    assertEquals( "5.6.7", theVersion.toString() );
    
    theVersion = new Version("1.2.3.4");
  }
}
