package chabernac.updater;

import java.io.Serializable;

public class Version implements Serializable{
  private String myVersion = null;
  public Version(String aVersion){
    myVersion = aVersion;
  }
  
  public String getVersion(){
    return myVersion;
  }

}
