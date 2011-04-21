package chabernac.comet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Endpoint;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;

/**
 * Servlet implementation class P2PServlet
 */
public class CometServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
//  private static Logger LOGGER = Logger.getLogger(CometServlet.class);

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
    if(aRequest.getParameter(  "show" ) != null){
      showEndPoints(aResponse);
      return;
    }
    
    String theId = aRequest.getParameter( "id" );

    if(theId == null) return;

    synchronized(theId){
      if(!myEndPoints.containsKey( theId)){
        myEndPoints.put( theId, new EndPoint(theId));
      }
    }

    EndPoint theEndPoint = myEndPoints.get(theId);

    try{
      if(aRequest.getParameterMap().containsKey("eventid")){
        //this is response to a comment event
        //look up the comet event and store the output in the comet event so that it can be processed
        String theEventId = aRequest.getParameter("eventid");
        String theEventOutput = aRequest.getParameter("eventoutput");
        if(myCometEvents.containsKey(theEventId)){
          myCometEvents.get(theEventId).setOutput(theEventOutput);
        }
        aResponse.getWriter().println( Responses.OK.name() );
      } else {
        //block untill an event for this endpoint (client) is available
        //the response will be send by the client in a next call in which an event id and event output is given as parameter (see code above)
        CometEvent theEvent = theEndPoint.getEvent();
        myCometEvents.put(theEvent.getId(), theEvent);
        aResponse.getWriter().println( myCometEventConverter.toString(theEvent) );
      }
    } catch ( Exception e ) {
      aResponse.getWriter().println(myCometEventConverter.toString(new CometEvent("-1", Responses.NO_DATA.name())));
    } 
    
    //remove the end point
    myEndPoints.remove( theId );
  }
  
  private void showEndPoints(HttpServletResponse aResponse) throws IOException{
    PrintWriter theWriter = aResponse.getWriter();
    for(EndPoint theEndPoint : myEndPoints.values()){
      theWriter.println(theEndPoint.getId());
    }
  }


  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet( request, response );
  }

  public Map<String, EndPoint> getEndPoints(){
    return myEndPoints;
  }
}
