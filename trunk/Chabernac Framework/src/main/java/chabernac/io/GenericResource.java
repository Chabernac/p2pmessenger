package chabernac.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

public class GenericResource extends AbstractResource {
  private static Logger LOGGER = Logger.getLogger(GenericResource.class);

  private static Class[] RESOURCE_CLASSES;

  static{
    RESOURCE_CLASSES = new Class[2];
    RESOURCE_CLASSES[0] = FileResource.class;
    RESOURCE_CLASSES[1] = JARResource.class;
    /*
    RESOURCE_CLASSES = Tools.getPackageClassesOfType(Package.getPackage("chabernac.io"), AbstractResource.class);
    Arrays.sort(RESOURCE_CLASSES, new ResourceClassComparator());
    LOGGER.debug("found resource classes:");
    for(int i=0;i<RESOURCE_CLASSES.length;i++){
      LOGGER.debug(RESOURCE_CLASSES[i].getName());
    }
    */
  }

  public static Class[] getResourceClasses(){
    return RESOURCE_CLASSES;
  }

  public static void setResourceClasses(Class[] aClasses){
    RESOURCE_CLASSES = aClasses;
  }

  public GenericResource(String anLocation) {
    super(anLocation);
  }

  public boolean exists() {
    return getExistingResource() != null;
  }

  private iResource getExistingResource(){
    for(int i=0;i<RESOURCE_CLASSES.length;i++){
      Class theClass = RESOURCE_CLASSES[i];
      if(theClass != getClass()){
        try{
          Constructor theConstructor = theClass.getConstructor(new Class[]{String.class});
          iResource theResource = (iResource)theConstructor.newInstance(new Object[]{getLocation()});
          if(theResource.exists()){
            return theResource;
          }
        }catch(Exception e){
          LOGGER.error("Could not create resource", e);
        }

      }
    }
    return null;
  }

  public InputStream getInputStream() throws IOException {
    iResource theResource = getExistingResource();
    if(theResource == null){
      throw new IOException("The resource: " + getLocation() + " could not be found");
    }
    return theResource.getInputStream();
  }

  public File getFile() throws IOException{
    iResource theExistingResource = getExistingResource();
    if(theExistingResource != null){
      return theExistingResource.getFile();
    }
    throw new FileNotFoundException("The file at location: " + getLocation() + " could not be found");
  }


  public static void main(String args[]){
    try{
      GenericResource theResource = new GenericResource("chabernac/io");
      if(theResource.exists()){
        File theFile = theResource.getFile();
        if(theFile.isDirectory()){
        File[] theFiles = theFile.listFiles();
        for(int i=0;i<theFiles.length;i++){
          System.out.println(theFiles[i].toString());
        }
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }

  }



}
