/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;

public class AcceptFileResponse {
  public static enum Response { ACCEPT, PENDING, REFUSED };
  private final File myFile;
  private final Response myResponse;
  private final String myTransferId;
  
  public AcceptFileResponse( String aTransferId, Response aResponse, File aFile ) {
    super();
    myTransferId = aTransferId;
    myFile = aFile;
    myResponse = aResponse;
  }

  public File getFile() {
    return myFile;
  }

  public Response getResponse() {
    return myResponse;
  }

  protected String getTransferId() {
    return myTransferId;
  }
}
