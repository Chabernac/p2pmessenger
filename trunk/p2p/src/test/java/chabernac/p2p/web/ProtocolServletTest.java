package chabernac.p2p.web;

import junit.framework.TestCase;

import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import chabernac.p2p.web.ProtocolServlet;

public class ProtocolServletTest extends TestCase {
  public void testP2PServlet() throws Exception{
    final ServletTester theServletTester = new ServletTester();
    theServletTester.setContextPath("/p2p");
    theServletTester.addServlet(ProtocolServlet.class, "/protocol");
    theServletTester.start();
    
    HttpTester theRequest = new HttpTester();
    theRequest.setMethod("GET");
    theRequest.setHeader("Host", "tester");
    theRequest.setURI("/p2p/protocol?session=1&input=ECO123");
    theRequest.setVersion("HTTP/1.0");
    
    String theResponse = theServletTester.getResponses(theRequest.generate());
    HttpTester theResponses = new HttpTester();
    theResponses.parse(theResponse);
    assertEquals(200,theResponses.getStatus());
    assertEquals("123\r\n", theResponses.getContent());
    
    
  }
}
