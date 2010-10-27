package chabernac.protocol.routing;

import java.io.IOException;

public class DummyPeer extends AbstractPeer {

  public DummyPeer(String anPeerId) {
    super(anPeerId);
  }

  private static final long serialVersionUID = 2935823404717544376L;

  @Override
  public String getEndPointRepresentation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSameEndPointAs(AbstractPeer aPeer) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isValidEndPoint() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected String sendMessage(String aMessage, int aTimeoutInSeconds)
      throws IOException {
    throw new IOException("Not supported");
  }

}
