package chabernac.protocol;

import chabernac.io.StreamSplittingServer;

public class P2PServerSplittingServerAdapter implements iP2PServer {
  private final StreamSplittingServer myServer;
  
  public P2PServerSplittingServerAdapter(StreamSplittingServer aServer) {
    super();
    myServer = aServer;
  }

  @Override
  public boolean start() {
    myServer.start();
    return true;
  }

  @Override
  public boolean isStarted() {
    return myServer.isStarted();
  }

  @Override
  public void stop() {
    myServer.close();
  }

  @Override
  public void kill() {
    myServer.kill();
  }

}
