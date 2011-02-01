package chabernac.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;

public class TempTest extends TestCase {
  public void testURLEncode() throws IOException, ClassNotFoundException{
    String a = "a b+c";
    String b = URLDecoder.decode(URLEncoder.encode(a, "UTF-8"), "UTF-8");
    assertEquals(a, b);
    
    byte[] theBytes = Base64.decodeBase64( "rO0ABXNyABpjaGFiZXJuYWMuY29tZXQuQ29tZXRFdmVudAng53QMqXcXAgADTAAEbXlJZHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wAB215SW5wdXRxAH4AAUwACG15T3V0cHV0cQB+AAF4cHQAAi0xdAAHTk9fREFUQXA=".getBytes());
    ObjectInputStream theInput = new ObjectInputStream(new ByteArrayInputStream(theBytes));
    Object theObject = theInput.readObject();
    System.out.println(theObject);
  }
}
