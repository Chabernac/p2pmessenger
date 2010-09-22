package chabernac.comet;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.eclipse.jetty.testing.ServletTester;

public class CometServletTest extends TestCase {
  public void testCometServlet() throws Exception{
    final ServletTester theServletTester = new ServletTester();
    theServletTester.setContextPath("/context");
    theServletTester.addServlet(CometServlet.class, "/servlet/comet");
    theServletTester.start();
    
    Thread.sleep(2000);
    
    Map<String, EndPoint> theEndPoints  = (Map<String, EndPoint>)theServletTester.getContext().getAttribute("EndPoints");

    assertNotNull(theEndPoints);
    
    final String theRequest=
      "GET /context/servlet/comet?id=1 HTTP/1.1\r\n"+
      "Host: tester\r\n"+
      "\r\n";     
               
   ExecutorService theService = Executors.newFixedThreadPool(1);
   theService.execute(new Runnable(){
     public void run(){
       try {
        System.out.println(theServletTester.getResponses(theRequest));
      } catch (Exception e) {
        fail("Should not have an exception");
      }
     }
   });
   
   Thread.sleep(1000);
   
   assertEquals(1, theEndPoints.size());
   
   EndPoint theEndPoint = theEndPoints.get("1");
   assertNotNull(theEndPoint);
   
   theEndPoint.setEvent(new CometEvent("event1", "input"));

  }
}
