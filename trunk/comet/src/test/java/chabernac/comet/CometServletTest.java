package chabernac.comet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;

public class CometServletTest extends TestCase {
  static{
    BasicConfigurator.configure();
  }
  
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
          assertEquals(200, response.getStatus());
          String theContent = response.getContent();
          CometEvent theEvent = theConverter.getObject(theContent);
          assertEquals("event1", theEvent.getId());
          assertEquals("input", theEvent.getInput());

          HttpTester theNewInput = new HttpTester();
          theNewInput.setMethod("GET");
          theNewInput.setHeader("Host","tester");
          theNewInput.setURI("/context/servlet/comet?id=1&eventid=event1&eventoutput=output");
          theNewInput.setVersion("HTTP/1.0");
          //        theNewInput.setContent( "");
          String theNewReplyString = theServletTester.getResponses( theNewInput.generate() );


          HttpTester theNewReply = new HttpTester();
          theNewReply.parse( theNewReplyString );
          assertEquals(200, response.getStatus());
          theContent = response.getContent();
          assertEquals( "OK", theContent);


        } catch (Exception e) {
          fail("Should not have an exception");
        }
      }
    });

    Thread.sleep(2000);

    EndPointContainer theEndPointContainer = (EndPointContainer)theServletTester.getContext().getServletContext().getAttribute("EndPoints");
    assertEquals(1, theEndPointContainer.size() );

    EndPoint theEndPoint = theEndPointContainer.getEndPointFor( "1", 5, TimeUnit.SECONDS);
    assertNotNull(theEndPoint);

    CometEvent theCometEvent = new CometEvent("event1", "input"); 
    theEndPoint.setEvent(theCometEvent);
    assertEquals( "output", theCometEvent.getOutput( 2000000 ));
  }

  public void testCorruptedEndPoints() throws Exception{
    fail("The test does not work at the moment");
    Server theServer  = new Server(8080);

    try{
      Context root = new Context(theServer,"/p2p",Context.SESSIONS);

      CometServlet theCometServlet = new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder( 1 );
      root.addServlet(theCometHolder, "/comet");
      theServer.start();

      //init a comet request

      URL theCometURL = new URL("http://localhost:8080/p2p/comet");
      final URLConnection theConnection = theCometURL.openConnection();
      theConnection.setDoOutput(true);
      OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
      theWriter.write("id=1");
      theWriter.flush();
      
      System.out.println("connection closing");
      Executors.newScheduledThreadPool(1).schedule(new Runnable(){
        public void run(){
         ((HttpURLConnection)theConnection).disconnect();
         try {
          theConnection.getInputStream().close();
          System.out.println("connection closed");
        } catch (IOException e) {
        }
        }
      }, 1, TimeUnit.SECONDS);

      try{
        theConnection.getInputStream();
      }catch(Exception e){
      }
      
//      BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
//      String theEvent = theReader.readLine();

      //at this point an endpoint must have been created

      assertTrue(theCometServlet.getEndPointContainer().containsEndPointFor("1"));

      CometEvent theEvent= new CometEvent("1", "input");
      //this end point is in fact invalid because the url connection has been closed
      //the comet servlet must now delegate the response to another end point
      EndPoint theEndPoint = theCometServlet.getEndPointContainer().getEndPointFor("1", 1, TimeUnit.SECONDS);
      theEndPoint.setEvent(theEvent);
      
      URLConnection theConnection2 = theCometURL.openConnection();
      theConnection2.setDoOutput(true);
      theWriter = new OutputStreamWriter(theConnection2.getOutputStream());
      theWriter.write("id=1");
      theWriter.flush();
      BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection2.getInputStream()));
      String theREvent = theReader.readLine();
      CometEvent theCometEvent = new Base64ObjectStringConverter<CometEvent>().getObject( theREvent );
      
      assertEquals("1", theCometEvent.getInput());
    } finally {
      theServer.stop();
    }


  }
}
