/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

public class UserInfoException extends Exception {

  private static final long serialVersionUID = -8715227235253521728L;

  public UserInfoException () {
    super();
  }

  public UserInfoException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public UserInfoException ( String anMessage ) {
    super( anMessage );
  }

  public UserInfoException ( Throwable anCause ) {
    super( anCause );
  }

}
