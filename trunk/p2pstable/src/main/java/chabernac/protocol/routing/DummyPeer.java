package chabernac.protocol.routing;


public class DummyPeer extends AbstractPeer {

  public DummyPeer(String anPeerId) {
    super(anPeerId);
  }

  private static final long serialVersionUID = 2935823404717544376L;

  @Override
  public String getEndPointRepresentation() {
    return "no end point for dummy peer";
  }

  @Override
  public boolean isSameEndPointAs(AbstractPeer aPeer) {
    return false;
  }

  @Override
  public boolean isValidEndPoint() {
    return false;
  }

  @Override
  public boolean isContactable() {
    return false;
  }
}
