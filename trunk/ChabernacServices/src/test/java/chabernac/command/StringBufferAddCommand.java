/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;


public class StringBufferAddCommand implements UndoableCommand {
  
  private StringBuffer myBuffer = null;
  private String myStringToAdd = null;
  
  public StringBufferAddCommand(StringBuffer aBuffer, String aStringToAdd){
    myBuffer = aBuffer;
    myStringToAdd = aStringToAdd;
  }

  @Override
  public void undo() throws CommandException {
    myBuffer.delete( myBuffer.length() - myStringToAdd.length(), myBuffer.length());
  }

  @Override
  public void execute() throws CommandException {
    myBuffer.append( myStringToAdd );
  }

}
