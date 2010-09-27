/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import java.io.IOException;
import java.util.Map;

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
import chabernac.protocol.routing.PeerSenderHolder;
import chabernac.tools.PropertyMap;

public class ProtocolServlet extends HttpServlet {
  private static final long serialVersionUID = -1872170586728725631L;
  private static Logger LOGGER = Logger.getLogger(ProtocolServlet.class);
  private ProtocolContainer myProtocolContainer = null;

  public void init() throws ServletException{
    try{
      PropertyMap thePropertyMap = new PropertyMap();
      thePropertyMap.setProperty("routingprotocol.exchangedelay", "-1");
      thePropertyMap.setProperty("routingprotocol.persist", "false");
      myProtocolContainer = new ProtocolContainer(new ProtocolFactory(thePropertyMap));

      ServerInfo theServerInfo = new ServerInfo(Type.WEB);
      //TODO retrieve from servlet parameter
      theServerInfo.setServerURL( "http:\\\\x22p0212:8080\\p2pwebnode\\" );
      myProtocolContainer.setServerInfo( theServerInfo );

      WebPeerSender theWebPeerSender = new WebPeerSender((Map<String, EndPoint>)getServletContext().getAttribute( "EndPoints" ));
      PeerSenderHolder.setPeerSender( theWebPeerSender  );
    }catch(Exception e){
      throw new ServletException("Could not init p2p servlet", e);
    }
  }

  public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse){
    String theInput = aRequest.getParameter(  "input" );
    String theSession = aRequest.getParameter( "session" );
    String theResult = myProtocolContainer.handleCommand( Long.parseLong( theSession ), theInput );
    try {
      aResponse.getWriter().println(theResult);
    } catch ( IOException e ) {
      LOGGER.error( "could not send response message ", e );
    }
  }
  
  public void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse){
    doGet(aRequest, aResponse);
  }
}
