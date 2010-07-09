/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

public class EncryptionException extends Exception {

  private static final long serialVersionUID = -311292248962309832L;

  public EncryptionException () {
    super();
  }

  public EncryptionException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public EncryptionException ( String anMessage ) {
    super( anMessage );
  }

  public EncryptionException ( Throwable anCause ) {
    super( anCause );
  }
}
