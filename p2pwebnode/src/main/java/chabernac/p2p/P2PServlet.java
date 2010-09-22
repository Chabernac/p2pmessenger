/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chabernac.comet.EndPoint;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.routing.PeerSenderHolder;
import chabernac.tools.PropertyMap;

public class P2PServlet extends HttpServlet {
  private static final long serialVersionUID = -1872170586728725631L;
  private static Logger LOGGER = Logger.getLogger(P2PServlet.class);
  private ProtocolContainer myProtocolContainer = null;
  
  public void init(){
    PropertyMap thePropertyMap = new PropertyMap();
    thePropertyMap.setProperty("routingprotocol.exchangedelay", "-1");
    thePropertyMap.setProperty("routingprotocol.persist", "false");
    myProtocolContainer = new ProtocolContainer(new ProtocolFactory(thePropertyMap));

    
    WebPeerSender theWebPeerSender = new WebPeerSender((Map<String, EndPoint>)getServletContext().getAttribute( "EndPoints" ));
    PeerSenderHolder.setPeerSender( theWebPeerSender  );
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
}
