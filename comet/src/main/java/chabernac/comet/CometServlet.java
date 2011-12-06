package chabernac.comet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.newcomet.EndPoint2;
import chabernac.newcomet.EndPointContainer2;

/**
 * Servlet implementation class P2PServlet
 */
public class CometServlet extends HttpServlet {
  private static final long serialVersionUID = 5572759442854877206L;
  private static final long MAX_EVENT_TIME = 30 * 1000;
  private static Logger LOGGER = Logger.getLogger(CometServlet.class);

  public static enum Responses{NO_DATA, OK};

  private iObjectStringConverter<CometEvent> myCometEventConverter =  new Base64ObjectStringConverter<CometEvent>();
  private CometEventExpirationListener myExpirationListener = new CometEventExpirationListener();

  private AtomicLong myConcurrentRequestCounter = new AtomicLong(0);

  private static long TIMEOUT_MINUTES = 15;

  public void init() throws ServletException{
    super.init();
    getEndPointContainer();

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable(){
      public void run(){
//        resetAllEndPoints();
      }
    }, TIMEOUT_MINUTES, TIMEOUT_MINUTES, TimeUnit.MINUTES);
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
    try{
//      LOGGER.debug( "Incrementing counter");
//      LOGGER.debug( "Concurrent requests in CometServlet: "  + myConcurrentRequestCounter.incrementAndGet());
      getCometEvents().removeOldEvents();
      if(aRequest.getParameter("clean") != null){
//        resetAllEndPoints();
      } if(aRequest.getParameter(  "show" ) != null){
        showEndPoints(aResponse);
      } else if(aRequest.getParameterMap().containsKey("eventid")){
        //this is response to a comment event
        //look up the comet event and store the output in the comet event so that it can be processed
        processEventResponse(aRequest, aResponse);
      } else {
        processIncomingEndPoint( aRequest, aResponse );
      }
    } catch ( Exception e ) {
      aResponse.getWriter().println(myCometEventConverter.toString(new CometEvent("-1", Responses.NO_DATA.name())));
    } finally {
      myConcurrentRequestCounter.decrementAndGet();
//      LOGGER.debug( "Decrementing counter");
//      LOGGER.debug( "Concurrent requests in CometServlet: "  + myConcurrentRequestCounter.get());
    }

  }

  private void processEventResponse(HttpServletRequest aRequest, HttpServletResponse aResponse) throws CometException, IOException{
    String theEventId = aRequest.getParameter("eventid");
    try{
      String theEventOutput = aRequest.getParameter("eventoutput");
      getCometEvents().setOutput(theEventId, theEventOutput );
      aResponse.getWriter().println( Responses.OK.name() );
    }finally{
      //remove the event from the stack
      getCometEvents().remove(theEventId);
    }
  }

  private void processIncomingEndPoint(HttpServletRequest aRequest, HttpServletResponse aResponse) throws InterruptedException, CometException{
    String theId = aRequest.getParameter( "id" );
    if(theId == null) return;

    //create a new endpoint
    EndPoint2 theEndPoint = getEndPointContainer().getEndPoint( theId );
    theEndPoint.setActive( true );
    try{
      theEndPoint.waitForEvent();
      
      while(theEndPoint.hasEvents()){
        Thread.yield();
        handleEvent( theEndPoint.getFirstEvent(), aResponse );
      }
      aResponse.getWriter().flush();
    }catch(Exception e){
      LOGGER.error("Could not send comet event to endpoint", e);
    } finally {
      theEndPoint.setActive( false );
    }
  }
  
  private void handleEvent(CometEvent anEvent, HttpServletResponse aResponse) throws IOException{
    anEvent.addExpirationListener(myExpirationListener);
    getCometEvents().addCommetEvent(anEvent);
    aResponse.getWriter().println( myCometEventConverter.toString(anEvent) );
  }

  private void showEndPoints(HttpServletResponse aResponse) throws IOException{
    PrintWriter theWriter = aResponse.getWriter();
    for(EndPoint2 theEndPoint : getEndPointContainer().getEndPoints()){
      theWriter.println(theEndPoint.toString());
    }
  }

  public CometEventContainer getCometEvents(){
    if(getServletContext().getAttribute( "CometEvents" ) == null){
      getServletContext().setAttribute( "CometEvents", new CometEventContainer() );
    }
    return (CometEventContainer)getServletContext().getAttribute( "CometEvents" );
  }



  public EndPointContainer2 getEndPointContainer(){
    if(getServletContext().getAttribute( "EndPoints" ) == null){
      getServletContext().setAttribute( "EndPoints", new EndPointContainer2() );
    }
    return (EndPointContainer2)getServletContext().getAttribute( "EndPoints" );
  }


  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet( request, response );
  }

  private class CometEventExpirationListener implements iCometEventExpirationListener{

    @Override
    public void cometEventExpired(CometEvent anEvent) {
      getCometEvents().remove(anEvent.getId());
    }

  }

}
