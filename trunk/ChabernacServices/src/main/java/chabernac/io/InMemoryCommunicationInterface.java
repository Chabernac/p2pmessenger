package chabernac.io;

import java.util.UUID;

public class InMemoryCommunicationInterface implements iCommunicationInterface {
  private final String myId = UUID.randomUUID().toString();
  
  private static class INSTANCE_HOLDER{
    public static InMemoryCommunicationInterface INSTANCE = new InMemoryCommunicationInterface();
  }
  
  private InMemoryCommunicationInterface(){
  }
  
  public static InMemoryCommunicationInterface getInstance(){
    return INSTANCE_HOLDER.INSTANCE;
  }

  
  @Override
  public String getId() {
    return myId;
  }

  @Override
  public String getName() {
    return "In JVM communication interface";
  }

}
