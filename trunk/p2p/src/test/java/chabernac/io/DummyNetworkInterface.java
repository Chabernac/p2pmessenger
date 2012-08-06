package chabernac.io;

import java.util.UUID;

import chabernac.io.iCommunicationInterface;

public class DummyNetworkInterface implements iCommunicationInterface {
  private final String myId = UUID.randomUUID().toString();
  
  @Override
  public String getId() {
    return myId;
  }

  @Override
  public String getName() {
    return "Dummy network interface";
  }

}
