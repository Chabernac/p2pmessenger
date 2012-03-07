/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import org.apache.log4j.Logger;

import chabernac.io.StreamSplittingServerException;
import chabernac.io.iSocketSender;
import chabernac.io.iStreamSplittingServerListener;
import chabernac.protocol.ServerInfo.Type;

public class StreamSplittingServerListener implements iStreamSplittingServerListener {
  private static final Logger LOGGER = Logger.getLogger(StreamSplittingServerListener.class);
  private final ProtocolContainer myProtocolContainer;

  public StreamSplittingServerListener ( ProtocolContainer aProtocolContainer ) {
    super();
    myProtocolContainer = aProtocolContainer;
  }

  @Override
  public void streamSplittingServerStarted( int aPort, iSocketSender aSocketSender ) throws StreamSplittingServerException{
    ServerInfo theServerInfo = new ServerInfo(Type.STREAM_SPLITTING_SOCKET);
    theServerInfo.setServerPort( aPort );
    theServerInfo.setSocketSender( aSocketSender );
    try{
      myProtocolContainer.setServerInfo( theServerInfo );
    }catch(ProtocolException e){
      LOGGER.error("An error occured while setting server info", e);
      throw new StreamSplittingServerException( "An error occured while setting server info", e );
    }
  }

  @Override
  public void streamSplittingServerStopped() {
  }
}
