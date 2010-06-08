/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeException;

public class PipeListener implements IPipeListener {
  private List< Pipe > myPipes = new ArrayList< Pipe >();

  @Override
  public void incomingPipe( Pipe aPipe ) throws PipeException {
    myPipes.add(aPipe);
  }
  
  public List<Pipe> getPipes(){
    return myPipes;
  }

}
