/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

public class FileTransferException extends Exception {

  private static final long serialVersionUID = 5282196180872854183L;

  public FileTransferException () {
    super();
  }

  public FileTransferException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public FileTransferException ( String anMessage ) {
    super( anMessage );
  }

  public FileTransferException ( Throwable anCause ) {
    super( anCause );
  }
}
