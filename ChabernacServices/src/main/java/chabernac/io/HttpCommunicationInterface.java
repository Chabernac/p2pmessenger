/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.util.UUID;

public class HttpCommunicationInterface implements iCommunicationInterface {

  private final String myId = UUID.randomUUID().toString();
  
  private static class INSTANCE_HOLDER{
    public static HttpCommunicationInterface INSTANCE = new HttpCommunicationInterface();
  }
  
  private HttpCommunicationInterface(){
  }
  
  public static HttpCommunicationInterface getInstance(){
    return INSTANCE_HOLDER.INSTANCE;
  }

  @Override
  public String getId() {
    return myId;
  }

  @Override
  public String getName() {
   return "Http communication interface";
  }

}
