package chabernac.tools;

import java.util.UUID;

public class InMemoryCommunicationInterface implements iCommunicationInterface {
  private final String myId = UUID.randomUUID().toString();
  
  @Override
  public String getId() {
    return myId;
  }

}
