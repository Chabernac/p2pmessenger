/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.stacktrace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;

public class ProcessProtocol extends Protocol{
  private static Logger LOGGER = Logger.getLogger(ProcessProtocol.class);

  public static final String ID = "PRC";

  public static enum Input{FULL_STACK_TRACE, CMD};
  public static enum Response{OK, NOK, INVALID_INPUT};

  public ProcessProtocol( ) {
    super( ID);
  }


  @Override
  public String getDescription() {
    return "Stack protocol";
  }

  private String execute(String aCMD) throws IOException{
    BufferedReader theReader = null;
    try{
      Process theProcess = Runtime.getRuntime().exec( aCMD );
      theReader = new BufferedReader( new InputStreamReader( theProcess.getInputStream()));
      StringBuilder theStack = new StringBuilder();
      String theLine = "";
      while((theLine = theReader.readLine()) != null){
        theStack.append( theLine );
        theStack.append("\r\n");
      }
      return theStack.toString();
    }finally{
      if(theReader != null){
        try {
          theReader.close();
        } catch ( IOException e ) {
          LOGGER.error("Could not clse reader", e);
        }
      }
    }
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    try{
      if(anInput.startsWith( Input.FULL_STACK_TRACE.name() )){
        String theJStackCMD = "jstack -l";
        if(anInput.length() > Input.FULL_STACK_TRACE.name().length()){
          theJStackCMD = anInput.substring( Input.FULL_STACK_TRACE.name().length() );
        }

        String thePID = ManagementFactory.getRuntimeMXBean().getName().split( "@" )[0];

        return execute( theJStackCMD + " " + thePID );
      }else if(anInput.startsWith( Input.CMD.name() )){
        String theCMD = anInput.substring( Input.CMD.name().length() );
        return execute( theCMD );
      }
    }catch(Exception e){
      LOGGER.error("Error occured in process", e);
      return Response.NOK.name() + " " + e.getMessage();
    }
    return Response.INVALID_INPUT.name();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
