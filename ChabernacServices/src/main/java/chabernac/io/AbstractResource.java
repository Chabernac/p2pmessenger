package chabernac.io;

import java.io.IOException;

import javax.activation.FileTypeMap;


public abstract class AbstractResource implements iResource {
  private static final long serialVersionUID = -928932666440994230L;
  
  protected String myLocation = null;
  
  public AbstractResource(String aLocation){
    myLocation = aLocation.replace('\\', '/');
  }

  public String getLocation() {
    return myLocation;
  }

  public void setLocation(String anLocation) {
    myLocation = anLocation;
  }
  
  public String getContentType(){
    return FileTypeMap.getDefaultFileTypeMap().getContentType(getLocation());
  }
  
  public String getName(){
    try {
      return getFile().getName();
    } catch (IOException e) {
      return null;
    }
  }
}
