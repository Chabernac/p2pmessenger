package chabernac.io;

import java.io.File;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter{
  private Vector myExtensions = null;
  
  public ExtensionFileFilter(){
    myExtensions = new Vector();
  }
  
  public void addExtension(String anExension){
    myExtensions.addElement(anExension.toLowerCase());
  }

  public boolean accept(File f) {
    for(int i=0;i<myExtensions.size();i++){
      if(f.getName().toLowerCase().endsWith((String)myExtensions.elementAt(i))) return true;
    }
    return false;
  }

  public String getDescription() {
    String theDescription = "Extension:";
    for(int i=0;i<myExtensions.size();i++){
      theDescription += " " + (String)myExtensions.elementAt(i);
    }
    return theDescription;
  }
  
}