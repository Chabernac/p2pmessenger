/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 15-jan-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      15-jan-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class CommandException extends RuntimeException {

  private static final long serialVersionUID = -4338066641993934649L;

  public CommandException () {
    super();
  }

  public CommandException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public CommandException ( String anMessage ) {
    super( anMessage );
  }

  public CommandException ( Throwable anCause ) {
    super( anCause );
  }

}
