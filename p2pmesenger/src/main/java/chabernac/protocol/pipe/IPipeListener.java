/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

public interface IPipeListener {
  public void incomingPipe(Pipe aPipe) throws PipeException;
}
