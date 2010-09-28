/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class SuperNodesDataSource implements DataSource{
  private final String[] mySuperNodes;
  
  public SuperNodesDataSource ( String ...anSuperNodes  ) {
    super();
    mySuperNodes = anSuperNodes;
  }

  @Override
  public String getContentType() {
    return "plain/text";
  }

  @Override
  public InputStream getInputStream() throws IOException {
    StringBuilder theBuilder = new StringBuilder();
    for(String theItem : mySuperNodes){
      theBuilder.append( theItem );
      theBuilder.append("\r\n");
    }
    return new ByteArrayInputStream(theBuilder.toString().getBytes()); 
  }

  @Override
  public String getName() {
    return "supernodes.txt";
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return null;
  }
}
