/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.encryption;

public class EncryptionException extends Exception {

  private static final long serialVersionUID = 9103956631885230348L;

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
