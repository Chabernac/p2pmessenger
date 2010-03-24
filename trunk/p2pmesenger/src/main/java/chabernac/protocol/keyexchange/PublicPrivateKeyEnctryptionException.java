/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

public class PublicPrivateKeyEnctryptionException extends Exception {

  private static final long serialVersionUID = -1443831253408552525L;

  public PublicPrivateKeyEnctryptionException () {
    super();
  }

  public PublicPrivateKeyEnctryptionException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public PublicPrivateKeyEnctryptionException ( String anMessage ) {
    super( anMessage );
  }

  public PublicPrivateKeyEnctryptionException ( Throwable anCause ) {
    super( anCause );
  }

}
