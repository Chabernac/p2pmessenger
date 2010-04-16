package chabernac.io;


public abstract class AbstractResource implements iResource {
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
}
