package chabernac.tools;

import java.util.UUID;

public class DummyNetworkInterface implements iNetworkInterface {
  private final String myId = UUID.randomUUID().toString();
  
  @Override
  public String getId() {
    return myId;
  }

}
