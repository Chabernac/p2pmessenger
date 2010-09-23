package chabernac.comet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
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
  private static final long serialVersionUID = 1L;
  private static Logger LOGGER = Logger.getLogger(CometServlet.class);

  public static enum Responses{NO_DATA, OK};

  private Map<String, EndPoint> myEndPoints = Collections.synchronizedMap( new HashMap<String, EndPoint>() );
  private Map<String, CometEvent> myCometEvents = Collections.synchronizedMap( new HashMap<String, CometEvent>() );
  
  private iObjectStringConverter<CometEvent> myCometEventConverter =  new Base64ObjectStringConverter<CometEvent>();

  public void init(ServletConfig aConfig){
    aConfig.getServletContext().setAttribute("EndPoints", myEndPoints);
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
    if(aRequest.getParameter("test") != null){
      aResponse.getWriter().println( Responses.OK.name() + aRequest.getParameter("test") );
    }
    
    String theId = aRequest.getParameter( "id" );
    
    if(theId == null) return;

    synchronized(theId){
      if(myEndPoints.containsKey( theId)){
        //there is already a request associated with this peer, unlock the other request and replace it with this one
        try {
          myEndPoints.get(theId).setEvent(new CometEvent("-1", Responses.NO_DATA.name() ));
        } catch (CometException e) {
          LOGGER.error("Unable to send no data response to endpoint", e);
        }
      }
      myEndPoints.put( theId, new EndPoint(theId));
    }
    
    EndPoint theEndPoint = myEndPoints.get(theId);
    
    if(aRequest.getParameterMap().containsKey("eventid")){
      String theEventId = aRequest.getParameter("eventid");
      String theEventOutput = aRequest.getParameter("eventoutput");
      if(myCometEvents.containsKey(theEventId)){
        myCometEvents.get(theEventId).setOutput(theEventOutput);
      }
      aResponse.getWriter().println( Responses.OK.name() );
    }

    try {
      CometEvent theEvent = theEndPoint.getEvent();
      myCometEvents.put(theEvent.getId(), theEvent);
      aResponse.getWriter().println( myCometEventConverter.toString(theEvent) );
    } catch ( Exception e ) {
      aResponse.getWriter().println(myCometEventConverter.toString(new CometEvent("-1", Responses.NO_DATA.name())));
    }
  }


  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet( request, response );
  }

}
