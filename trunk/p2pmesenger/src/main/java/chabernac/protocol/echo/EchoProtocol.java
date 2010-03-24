package chabernac.protocol.echo;
import chabernac.protocol.Protocol;

/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */

public class EchoProtocol extends Protocol {
  public static final String ID = "ECO";

  public EchoProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Echo Protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    return anInput;
  }

  @Override
  public void stop() {
  }

}
