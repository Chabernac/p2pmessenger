package chabernac.comet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class P2PServlet
 */
public class CometServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  public static enum Responses{NO_DATA};
  
  private Map<String, EndPoint> myEndPoints = Collections.synchronizedMap( new HashMap<String, EndPoint>() );
  
  private iDataHandler myDataHandler = null;

  /**
   * Default constructor. 
   */
  public CometServlet() {
    myDataHandler = (iDataHandler)getServletContext().getAttribute( "datahandler" );
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String theId = request.getParameter( "id" );
    
    EndPoint theEndPoint = new EndPoint(theId);
    synchronized(theId){
      if(myEndPoints.containsKey( theEndPoint.getId() )){
        //there is already a request associated with this peer, unlock the other request and replace it with this one
        myEndPoints.get(theEndPoint.getId()).setData( Responses.NO_DATA.name() );
      }
      myEndPoints.put( theEndPoint.getId(), theEndPoint);
    }
    
    if(request.getParameter( "data" )  != null){
      myDataHandler.handleData( request.getParameter( "data"), myEndPoints );
    }
    
    try {
      response.getWriter().println( theEndPoint.getData() );
    } catch ( InterruptedException e ) {
      response.getWriter().println(Responses.NO_DATA.name());
    }
  }
  

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet( request, response );
  }

}
