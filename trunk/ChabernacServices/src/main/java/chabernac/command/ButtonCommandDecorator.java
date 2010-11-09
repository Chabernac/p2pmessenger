/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

public class ButtonCommandDecorator extends AbstractCommand {
  private final Command myCommand;
  private final String myName;
  private char myMnemonic;

  public ButtonCommandDecorator( Command aCommand, String aName, char aMnemonic ) {
    super();
    myCommand = aCommand;
    myMnemonic = aMnemonic;
    myName = aName;
  }

  @Override
  public char getMnemonic() {
    return myMnemonic;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void execute() {
    myCommand.execute();
  }

}
