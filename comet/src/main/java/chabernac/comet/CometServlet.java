package chabernac.comet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
  private List<EndPoint> myPendingEndPoints = Collections.synchronizedList( new ArrayList<EndPoint>() );

  private AtomicLong myConcurrentRequestCounter = new AtomicLong(0);

  private static long TIMEOUT_MINUTES = 15;

  public void init() throws ServletException{
    super.init();
    getEndPointContainer();

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable(){
      public void run(){
        resetAllEndPoints();
      }
    }, TIMEOUT_MINUTES, TIMEOUT_MINUTES, TimeUnit.MINUTES);
  }

  private void resetAllEndPoints(){
    for(EndPoint theEndPoint : myPendingEndPoints){
      try {
//        LOGGER.debug("resetting end point '" + theEndPoint.getId() + "'");
        theEndPoint.setEvent(new CometEvent(UUID.randomUUID().toString(),  CometServlet.Responses.NO_DATA.name()));
      } catch (CometException e) {
        LOGGER.error("Could not reset end point for '" + theEndPoint.getId() + "'");
      }
    }
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
        resetAllEndPoints();
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
      LOGGER.error("Error occured while processing event", e);
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
      getCometEvents().setOutput(theEventId, theEventOutput);
      aResponse.getWriter().println( Responses.OK.name() );
    }finally{
      //remove the event from the stack
      getCometEvents().remove(theEventId);
    }
  }

  private void processIncomingEndPoint(HttpServletRequest aRequest, HttpServletResponse aResponse) throws InterruptedException, CometException{
    String theId = aRequest.getParameter( "id" );
    if(theId == null) return;

    CometEvent theEvent = null;
    //create a new endpoint
    EndPoint theEndPoint = new EndPoint( theId );
    try{
      myPendingEndPoints.add( theEndPoint );
      //block untill an event for this endpoint (client) is available
      //the response will be send by the client in a next call in which an event id and event output is given as parameter (see code above)

      //publish the end point so that other processes can detecte it and put data for this end point
      getEndPointContainer().addEndPoint( theEndPoint );

      theEvent = theEndPoint.getEvent();
      theEvent.addExpirationListener(myExpirationListener);
      getCometEvents().addCommetEvent(theEvent);
      LOGGER.debug("Sending comet event '" + theEvent.getId() + "' to peer '" + theId + "'");
      aResponse.getWriter().println( myCometEventConverter.toString(theEvent) );
      aResponse.getWriter().flush();
    }catch(Exception e){
      LOGGER.error("Could not send comet event to endpoint", e);
      if(theEvent != null){
        EndPoint theOtherEndPoint = getEndPointContainer().getEndPointFor(theId, 1, TimeUnit.SECONDS);
        if(theOtherEndPoint == null){
          theEvent.setOutput( new EndPointNotAvailableException("Could not send comet event to endpoint", e) );
        } else {
          theOtherEndPoint.setEvent(theEvent);
        }
      }
    } finally {
      //we 're doing something double here, but it seems like the endpoint container does not have references to all pending end points
      myPendingEndPoints.remove( theEndPoint );
      getEndPointContainer().removeEndPoint(theEndPoint);
    }
  }

  private void showEndPoints(HttpServletResponse aResponse) throws IOException{
    PrintWriter theWriter = aResponse.getWriter();
    for(EndPoint theEndPoint : myPendingEndPoints){
      theWriter.println(theEndPoint.getId());
    }
  }

  public CometEventContainer getCometEvents(){
    if(getServletContext().getAttribute( "CometEvents" ) == null){
      getServletContext().setAttribute( "CometEvents", new CometEventContainer() );
    }
    return (CometEventContainer)getServletContext().getAttribute( "CometEvents" );
  }



  public EndPointContainer getEndPointContainer(){
    if(getServletContext().getAttribute( "EndPoints" ) == null){
      getServletContext().setAttribute( "EndPoints", new EndPointContainer() );
    }
    return (EndPointContainer)getServletContext().getAttribute( "EndPoints" );
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
