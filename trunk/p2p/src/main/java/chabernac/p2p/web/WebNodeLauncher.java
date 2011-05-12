/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.web;


import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacade;
import chabernac.utils.ArgsInterPreter;

public class WebNodeLauncher {
  private static Logger LOGGER = Logger.getLogger(WebNodeLauncher.class);

  public static void main(String[] args){
    BasicConfigurator.configure();
    ArgsInterPreter theInterpreter = new ArgsInterPreter( args );
    
    try{
      if(!theInterpreter.containsKey( "port" )) throw new IllegalArgumentException( "You must specify a port with port=");
      if(!theInterpreter.containsKey( "url" )) throw new IllegalArgumentException("You must specify an url with url=");
      if(!theInterpreter.containsKey( "persist" )) throw new IllegalArgumentException("You must specify the persistance with persistance=[true|false]");
      
      System.out.println("Attempting to start webnode at port: '" + theInterpreter.getKeyValue( "port" ) + " and published at url '" + theInterpreter.getKeyValue( "url" ) + " with persistance " + theInterpreter.getKeyValue("persist"));
      
      URL theURL = new URL(theInterpreter.getKeyValue( "url" ));
      
      P2PFacade theFacade = new P2PFacade()
      .setWebNode( true )
      .setWebPort( Integer.parseInt( theInterpreter.getKeyValue( "port" )) )
      .setWebURL( theURL )
      .setExchangeDelay( 300 )
      .setPersist( Boolean.valueOf( theInterpreter.getKeyValue("persist") ));
      
      if(theInterpreter.containsKey( "ajpport" )){
        theFacade.setAJPPort( Integer.parseInt( theInterpreter.getKeyValue( "ajpport" ) ) );
      }
      
      theFacade.start( );
      
      System.out.println("Webnode started!");
    }catch(Throwable e){
      System.out.println("An error occured while starting server, consult log file for more details");
      LOGGER.error("Could not start server", e);
      System.exit( -1 );
    }
  }
}
