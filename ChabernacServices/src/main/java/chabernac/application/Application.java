package chabernac.application;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import chabernac.io.FileExtensionFilter;
import chabernac.utils.ServiceTools;



public class Application implements Serializable, iApplication{
  private static final Logger LOGGER  = Logger.getLogger(Application.class);
  public static final long serialVersionUID = 5333394425367644318L; 
  
  
  private String name = "";
  private String version = "";
  private String main = "";
  private String description = "";
  private String notes = "";
  private String parameters = "";
  private ArrayList jars = new ArrayList();
  private boolean isDebugEnabled = false;


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
  public void addAllJarsInDirectory(File aDirectory){
    ArrayList theJars = new ArrayList();
    
    ServiceTools.findFiles(theJars, aDirectory, new FileExtensionFilter(new String[]{"jar"}), true);
    for(Iterator i=theJars.iterator();i.hasNext();){
      File theFile = (File)i.next();
      try {
        addJar(theFile.getCanonicalPath());
      } catch (IOException e) {
        LOGGER.error("An error occured while retrieving canonical path for: " + theFile.getAbsolutePath());
      }
    }
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
    LOGGER.debug("Executing command: " + theCommand);
    try{
      //Runtime.getRuntime().exec(theCommand, null, new File(""));
      Runtime.getRuntime().exec(theCommand);
    }catch(IOException e){
      LOGGER.error("Could not start application: " + name, e);
    }
  }
  
}
