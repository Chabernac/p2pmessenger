package chabernac.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import junit.framework.TestCase;

public class TempTest extends TestCase {
  public void testURLEncode() throws UnsupportedEncodingException{
    String a = "a b+c";
    String b = URLDecoder.decode(URLEncoder.encode(a, "UTF-8"), "UTF-8");
    assertEquals(a, b);
  }
}
