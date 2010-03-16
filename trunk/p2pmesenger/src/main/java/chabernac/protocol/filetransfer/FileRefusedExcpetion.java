/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 16-mrt-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      16-mrt-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class FileRefusedExcpetion extends Exception {

  private static final long serialVersionUID = -4537575579395442688L;

  public FileRefusedExcpetion () {
    super();
  }

  public FileRefusedExcpetion ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public FileRefusedExcpetion ( String anMessage ) {
    super( anMessage );
  }

  public FileRefusedExcpetion ( Throwable anCause ) {
    super( anCause );
  }
}
