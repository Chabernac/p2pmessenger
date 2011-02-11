/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.document;

import java.io.File;
import java.io.Serializable;

public class DocumentationBase implements Serializable{
  private static final long serialVersionUID = -2188267434937014476L;
  private String myName;
  private File myLocation;
  
  public DocumentationBase( String aName, File aLocation ) {
    super();
    myName = aName;
    myLocation = aLocation;
    if(!myLocation.isDirectory()) throw new IllegalArgumentException("Location must be directory");
  }
  public String getName() {
    return myName;
  }
  public void setName( String aName ) {
    myName = aName;
  }
  public File getLocation() {
    return myLocation;
  }
  public void setLocation( File aLocation ) {
    if(!aLocation.isDirectory()) throw new IllegalArgumentException("Location must be directory");
    myLocation = aLocation;
  }
  
  public String toString(){
    return myName + "=" + myLocation.toString();
  }
}
