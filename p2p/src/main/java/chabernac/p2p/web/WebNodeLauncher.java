/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.web;


import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.ajp.Ajp13SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.utils.ArgsInterPreter;

public class WebNodeLauncher {
  private static Logger LOGGER = Logger.getLogger(WebNodeLauncher.class);

  public static void main(String[] args){
    BasicConfigurator.configure();
    ArgsInterPreter theInterpreter = new ArgsInterPreter( args );
    
    if(!theInterpreter.containsKey( "port" )) System.out.println("You must specify a port with port=");
    if(!theInterpreter.containsKey( "url" )) System.out.println("You must specify an url with url=");
    if(!theInterpreter.containsKey( "persist" )) System.out.println("You must specify the persistance with persistance=[true|false]");
    
    
    try{
      System.out.println("Attempting to start webnode at port: '" + theInterpreter.getKeyValue( "port" ) + " and published at url '" + theInterpreter.getKeyValue( "url" ) + " with persistance " + theInterpreter.getKeyValue("persist"));
      
      URL theURL = new URL(theInterpreter.getKeyValue( "url" ));
      Server theServer = new Server(Integer.parseInt( theInterpreter.getKeyValue( "port" )));
      Ajp13SocketConnector theAJPConnector = new Ajp13SocketConnector();
      theAJPConnector.setPort(Integer.parseInt(theInterpreter.getKeyValue("ajpport", "8090")));
      theServer.addConnector(theAJPConnector);
      Context root = new Context(theServer,"/p2p",Context.SESSIONS);
      
      CometServlet theCometServlet = new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder( 1 );
      root.addServlet(theCometHolder, "/comet");

      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      theProtocolHolder.setInitParameter("persist", theInterpreter.getKeyValue("persist"));
      root.addServlet(theProtocolHolder, "/protocol");
      theProtocolHolder.setInitParameter( "serverurl", theURL.toString() );

      theServer.start();
      System.out.println("Webnode started!");
    }catch(Throwable e){
      System.out.println("An error occured while starting server, consult log file for more details");
      LOGGER.error("Could not start server", e);
      System.exit( -1 );
    }
  }
}
