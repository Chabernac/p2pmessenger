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
public interface UndoableCommand extends Command {
  public void undo() throws CommandException;
}
