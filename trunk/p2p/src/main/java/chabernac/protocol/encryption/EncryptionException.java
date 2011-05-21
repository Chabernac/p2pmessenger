/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.encryption;

public class EncryptionException extends Exception {

  private static final long serialVersionUID = -311292248962309832L;
  
  public static enum Reason{ENCRYPTED_USING_BAD_PUBLIC_KEY, OTHER};

  private Reason myReason = Reason.OTHER; 
  
  public EncryptionException () {
    super();
  }

  public EncryptionException ( Reason aReason, String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
    myReason = aReason;
  }
  
  public EncryptionException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }
  
  public EncryptionException ( Reason aReason, String anMessage  ) {
    super( anMessage);
    myReason = aReason;
  }

  public EncryptionException ( String anMessage ) {
    super( anMessage );
  }

  public EncryptionException ( Throwable anCause ) {
    super( anCause );
  }

  public Reason getReason() {
    return myReason;
  }
}
