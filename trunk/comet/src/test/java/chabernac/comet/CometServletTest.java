package chabernac.comet;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;

public class CometServletTest extends TestCase {
  public void testCometServlet() throws Exception{
    final iObjectStringConverter<CometEvent> theConverter = new Base64ObjectStringConverter<CometEvent>();
    
    final ServletTester theServletTester = new ServletTester();
    theServletTester.setContextPath("/context");
    theServletTester.addServlet(CometServlet.class, "/servlet/comet");
    theServletTester.start();
    
    
    final String theRequest=
      "GET /context/servlet/comet?id=1 HTTP/1.1\r\n"+
      "Host: tester\r\n"+
      "\r\n";     
               
   ExecutorService theService = Executors.newFixedThreadPool(1);
   theService.execute(new Runnable(){
     public void run(){
       try {
        HttpTester response = new HttpTester();
        response.parse(theServletTester.getResponses(theRequest));
        assertEquals("200", response.getStatus());
        String theContent = response.getContent();
        CometEvent theEvent = theConverter.getObject(theContent);
        assertEquals("event1", theEvent.getId());
        assertEquals("input", theEvent.getInput());
      } catch (Exception e) {
        fail("Should not have an exception");
      }
     }
   });
   
   Thread.sleep(1000);
   
   Map<String, EndPoint> theEndPoints  = (Map<String, EndPoint>)theServletTester.getContext().getServletContext().getAttribute("EndPoints");
   assertEquals(1, theEndPoints.size());
   
   EndPoint theEndPoint = theEndPoints.get("1");
   assertNotNull(theEndPoint);
   
   theEndPoint.setEvent(new CometEvent("event1", "input"));
   
   Thread.sleep(2000);

  }
}
