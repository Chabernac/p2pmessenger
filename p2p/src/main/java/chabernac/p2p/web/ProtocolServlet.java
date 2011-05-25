/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chabernac.comet.EndPointContainer;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ServerInfo;
import chabernac.protocol.ServerInfo.Type;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.SessionData;
import chabernac.protocol.routing.WebPeer;
import chabernac.protocol.routing.WebRoutingTableInspecter;
import chabernac.tools.PropertyMap;

public class ProtocolServlet extends HttpServlet {
  private static final long serialVersionUID = -1872170586728725631L;
  private static Logger LOGGER = Logger.getLogger(ProtocolServlet.class);
  private AtomicLong myConcurrentRequestCounter = new AtomicLong(0);

  public void init() throws ServletException{
    super.init();
    try{
      if(getServletContext().getAttribute("SessionData") == null){
        getServletContext().setAttribute("SessionData", new SessionData());
      }

      if(getServletContext().getAttribute("PeerIpMap") == null){
        getServletContext().setAttribute("PeerIpMap", new HashMap<String, String>());
      }

      if(getServletContext().getAttribute( "ProtocolContainer") == null){
        PropertyMap thePropertyMap = new PropertyMap();
        thePropertyMap.setProperty("routingprotocol.exchangedelay", "60");
        thePropertyMap.setProperty("routingprotocol.persist", "true".equalsIgnoreCase(getInitParameter("persist")));

        Set<String> theSupportedProtocols = new HashSet< String >();
        theSupportedProtocols.add( RoutingProtocol.ID );
        theSupportedProtocols.add( MessageProtocol.ID );
        theSupportedProtocols.add( EchoProtocol.ID );

        ProtocolContainer theProtocolContainer = new ProtocolContainer(new ProtocolFactory(thePropertyMap), theSupportedProtocols);

        getServletContext().setAttribute( "ProtocolContainer", theProtocolContainer );
      }
      
      ServerInfo theServerInfo = new ServerInfo(Type.WEB);
      theServerInfo.setServerURL( getServletConfig().getInitParameter( "serverurl" ));

      getProtocolContainer().setServerInfo(theServerInfo);

      WebPeer theWebPeer = (WebPeer)((RoutingProtocol)getProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable().getEntryForLocalPeer().getPeer();
      theWebPeer.setEndPointContainer( (EndPointContainer)getServletContext().getAttribute( "EndPoints" ) );
      
      try{
        RoutingProtocol theRoutingProtocol = (RoutingProtocol)getProtocolContainer().getProtocol(RoutingProtocol.ID);
        WebRoutingTableInspecter theInspector = new WebRoutingTableInspecter(getSessionData(), getPeerIpMap());
        theRoutingProtocol.setRoutingTableInspector(theInspector);
      }catch(Exception e){
        LOGGER.error( "Unable to get routingprotocol", e );
      }

    }catch(Exception e){
      throw new ServletException("Could not init p2p servlet", e);
    }
  }

  public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse){
    String theInput = aRequest.getParameter(  "input" );
    String theSession = aRequest.getParameter( "session" );
    String thePeerId = aRequest.getParameter("peerid");


    LOGGER.debug( "Received message from peer '" + thePeerId + "' in session '" + theSession + "': " + theInput + "'" + " at remote ip '" + aRequest.getRemoteAddr() + "'" );
    //TODO remove when logging correctly enabled on server
    //    System.out.println("Received message from peer '" + thePeerId + "' in session '" + theSession + "': " + theInput + "'" );

    try {
      LOGGER.debug( "Concurrent requests in ProtocolServlet: "  + myConcurrentRequestCounter.incrementAndGet());
      
      if("exchange".equalsIgnoreCase( theInput ) ){
        ((RoutingProtocol)getProtocolContainer().getProtocol( RoutingProtocol.ID )).exchangeRoutingTable();
      }else if(theInput == null || "".equals( theInput ) || theSession == null || "".equals( theSession )){
        printDebugInfo(aRequest, aResponse);
      } else {
        getPeerIpMap().put(thePeerId, aRequest.getRemoteAddr());
        getSessionData().putProperty(theSession, "requestor.ip", aRequest.getRemoteAddr());
        
        String theResult = getProtocolContainer().handleCommand(theSession , theInput );
        aResponse.getWriter().println(theResult);
      }
    } catch ( Exception e ) {
      LOGGER.error( "could not send response message ", e );
    } finally {
      //remove the session data
      getSessionData().clearSessionData( theSession );
      myConcurrentRequestCounter.decrementAndGet();
    }
  }
  
  private void printDebugInfo(HttpServletRequest aRequest, HttpServletResponse aResponse) throws IOException, ProtocolException{
    aResponse.getWriter().println("Routing table");
    aResponse.getWriter().println("");
    aResponse.getWriter().println( ((RoutingProtocol)getProtocolContainer().getProtocol( RoutingProtocol.ID )).getLocalPeerId() );
    aResponse.getWriter().println( ((RoutingProtocol)getProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable() );
    aResponse.getWriter().println("Peer ip map");
    aResponse.getWriter().println("");
    for(String thePeer : getPeerIpMap().keySet()){
      aResponse.getWriter().println("Peer '" + thePeer + "' has ip '" + getPeerIpMap().get(thePeer) + "'");
    }

    
  }

  public void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse){
    doGet(aRequest, aResponse);
  }

  public ProtocolContainer getProtocolContainer(){
    return (ProtocolContainer)getServletContext().getAttribute( "ProtocolContainer" );
  }

  public SessionData getSessionData(){
    return (SessionData)getServletContext().getAttribute( "SessionData" );
  }

  public Map<String, String> getPeerIpMap(){
    return (Map<String, String>)getServletContext().getAttribute( "PeerIpMap" );
  }
}
