package chabernac.updater;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import chabernac.log.Logger;

public class Application implements Serializable, iApplication{
  public static final long serialVersionUID = 5333394425367644318L; 
  
  
  private String name = "";
  private String version = "";
  private String main = "";
  private String description = "";
  private String notes = "";
  private String parameters = "";
  private boolean isDebugEnabled = false;
  private ArrayList jars = new ArrayList();


  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public void addJar(String aJar){
    jars.add(aJar);
  }
  public void removeModule(String aJar){
    jars.remove(aJar);
  }
  public String getMain() {
    return main;
  }
  public void setMain(String main) {
    this.main = main;
  }
  public ArrayList getJars(){
    return jars;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getNotes() {
    return notes;
  }
  public void setNotes(String notes) {
    this.notes = notes;
  }
  
  public void addParameter(String aParameter){
    parameters += " " + aParameter;
  }
  
  public void addParameter(String aKey, String aParameter){
    addParameter(aParameter);
  }
  public boolean isDebugEnabled() {
    return isDebugEnabled;
  }
  public void setDebugEnabled(boolean anIsDebugEnabled) {
    isDebugEnabled = anIsDebugEnabled;
  }
  public void runApplication(){
    String theCommand = "cmd /c javaw ";
    if(isDebugEnabled){
      //theCommand += " -Xdebug -Xrunjdwp:transport=dt_shmem,address=8666,server=y,suspend=n ";
      theCommand += " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8666,server=y,suspend=n ";
    }
    theCommand += " -classpath \"";
    for(int i=0;i<jars.size();i++) {
      theCommand += (String)jars.get(i) + ";";
    }
    theCommand += ".\" " + main + " " + parameters;
    Logger.log(this,"Executing command: " + theCommand);
    try{
      //Runtime.getRuntime().exec(theCommand, null, new File(""));
      Runtime.getRuntime().exec(theCommand);
    }catch(IOException e){
      Logger.log(this,"Could not start application: " + name, e);
    }
  }
}
