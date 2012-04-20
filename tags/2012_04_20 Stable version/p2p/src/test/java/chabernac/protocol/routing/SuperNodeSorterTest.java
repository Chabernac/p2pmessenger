package chabernac.protocol.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class SuperNodeSorterTest extends TestCase {
  public void testSuperNodeSorter(){
    List<String> theSuperNodes = new ArrayList<String>();
    theSuperNodes.add("10.0.0.2");
    theSuperNodes.add("10.0.0.1");
    theSuperNodes.add("http://www.test.be");
    theSuperNodes.add("10.0.0.3");
    
    Collections.sort(theSuperNodes, new SuperNodeSorter());
    
    assertEquals("http://www.test.be", theSuperNodes.get(0));
    assertEquals("10.0.0.1", theSuperNodes.get(1));
    assertEquals("10.0.0.2", theSuperNodes.get(2));
    assertEquals("10.0.0.3", theSuperNodes.get(3));
  }
}
