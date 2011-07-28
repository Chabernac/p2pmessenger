package chabernac.p2p.web;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class ProtocolServletTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testP2PServlet() throws Exception{
    final ServletTester theServletTester = new ServletTester();
    theServletTester.setContextPath("/p2p");
    ServletHolder theHandler = theServletTester.addServlet(ProtocolServlet.class, "/protocol");
    theHandler.setInitParameter( "serverurl", "http://localhost:8080/" );
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
