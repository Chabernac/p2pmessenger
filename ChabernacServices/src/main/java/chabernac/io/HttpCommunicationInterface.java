/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.util.UUID;

public class HttpCommunicationInterface implements iCommunicationInterface {

  private final String myId = UUID.randomUUID().toString();

  @Override
  public String getId() {
    return myId;
  }

  @Override
  public String getName() {
   return "Http communication interface";
  }

}
