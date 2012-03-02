package chabernac.protocol;

import java.util.UUID;

import chabernac.io.StreamSplittingServer;
import chabernac.io.iInputOutputHandler;
import chabernac.protocol.routing.SessionData;

public class InputOutputProtocolAdapter implements iInputOutputHandler{
  private final IProtocol myProtocol;
  private StreamSplittingServer myStreamSplittingServer;
  private final SessionData mySessionData;

  public InputOutputProtocolAdapter(IProtocol aProtocol) {
    super();
    myProtocol = aProtocol;
    mySessionData = aProtocol.getSessionData();
  }
  
  public void setStreamSplittingServer( StreamSplittingServer aStreamSplittingServer ) {
    myStreamSplittingServer = aStreamSplittingServer;
  }

  @Override
  public String handle(String anId, String anInput) {
    String theSessionId = UUID.randomUUID().toString();
    mySessionData.putProperty( theSessionId, ProtocolServer.REMOTE_IP, myStreamSplittingServer.getSocket( anId ).getInetAddress().getHostAddress() );
    return myProtocol.handleCommand(theSessionId, anInput);
  }

  @Override
  public void close() {
    myProtocol.stop();
  }
  
  
}
