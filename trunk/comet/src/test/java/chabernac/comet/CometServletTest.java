package chabernac.comet;

import org.eclipse.jetty.testing.ServletTester;

import junit.framework.TestCase;

public class CometServletTest extends TestCase {
  public void testCometServlet() throws Exception{
    ServletTester theServletTester = new ServletTester();
    theServletTester.setContextPath("/context");
    theServletTester.addServlet("come.acme.TestFilter", "/*");
    theServletTester.addServlet(CometServlet.class, "/servlet/comet");
    theServletTester.start();

    String requests=
      "GET /context/servlet/comet HTTP/1.1\r\n"+
      "Host: tester\r\n"+
      "\r\n"+         
      "GET /context/hello HTTP/1.1\r\n"+
      "Host: tester\r\n"+
      "\r\n";
               
   String responses = theServletTester.getResponses(requests);
   System.out.println(responses);

  }
}
