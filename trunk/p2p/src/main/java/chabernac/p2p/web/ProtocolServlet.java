/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chabernac.comet.EndPoint;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ServerInfo;
import chabernac.protocol.ServerInfo.Type;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.SessionData;
import chabernac.protocol.routing.WebRoutingTableInspecter;
import chabernac.tools.PropertyMap;

public class ProtocolServlet extends HttpServlet {
  private static final long serialVersionUID = -1872170586728725631L;
  private static Logger LOGGER = Logger.getLogger(ProtocolServlet.class);

  public void init() throws ServletException{
    try{
      if(getServletContext().getAttribute("SessionData") == null){
        getServletContext().setAttribute("SessionData", new SessionData());
      }

      if(getServletContext().getAttribute("PeerIpMap") == null){
        getServletContext().setAttribute("PeerIpMap", new HashMap<String, String>());
      }

      if(getServletContext().getAttribute( "ProtocolContainer") == null){
        PropertyMap thePropertyMap = new PropertyMap();
        thePropertyMap.setProperty("routingprotocol.exchangedelay", "-1");
        thePropertyMap.setProperty("routingprotocol.persist", "false");
        thePropertyMap.setProperty("routingprotocol.peersender", new WebPeerSender((Map<String, EndPoint>)getServletContext().getAttribute( "EndPoints" )));

        Set<String> theSupportedProtocols = new HashSet< String >();
        theSupportedProtocols.add( RoutingProtocol.ID );
        theSupportedProtocols.add( MessageProtocol.ID );
        theSupportedProtocols.add( EchoProtocol.ID );

        ProtocolContainer theProtocolContainer = new ProtocolContainer(new ProtocolFactory(thePropertyMap), theSupportedProtocols);

        ServerInfo theServerInfo = new ServerInfo(Type.WEB);
        theServerInfo.setServerURL( getServletConfig().getInitParameter( "serverurl" ));
        theProtocolContainer.setServerInfo( theServerInfo );

        try{
          RoutingProtocol theRoutingProtocol = (RoutingProtocol)theProtocolContainer.getProtocol(RoutingProtocol.ID);
          WebRoutingTableInspecter theInspector = new WebRoutingTableInspecter(getSessionData(), getPeerIpMap());
          theRoutingProtocol.setRoutingTableInspector(theInspector);
        }catch(Exception e){
          LOGGER.error( "Unable to get routingprotocol", e );
        }

        getServletContext().setAttribute( "ProtocolContainer", theProtocolContainer );
      }

    }catch(Exception e){
      throw new ServletException("Could not init p2p servlet", e);
    }
  }

  public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse){
    String theInput = aRequest.getParameter(  "input" );
    String theSession = aRequest.getParameter( "session" );
    String thePeerId = aRequest.getParameter("peerid");

    getPeerIpMap().put(thePeerId, aRequest.getRemoteAddr());
    getSessionData().putProperty(theSession, "requestor.ip", aRequest.getRemoteAddr());

    try {
      if(theInput == null || "".equals( theInput ) || theSession == null || "".equals( theSession )){
        aResponse.getWriter().println( ((RoutingProtocol)getProtocolContainer().getProtocol( RoutingProtocol.ID )).getLocalPeerId() );
      } else {
        String theResult = getProtocolContainer().handleCommand( Long.parseLong( theSession ), theInput );
        aResponse.getWriter().println(theResult);
      }
    } catch ( Exception e ) {
      LOGGER.error( "could not send response message ", e );
    } finally {
      //remove the session data
      getSessionData().clearSessionData( theSession );
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
