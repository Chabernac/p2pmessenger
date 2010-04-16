package chabernac.io;

import java.io.File;
import java.io.FileFilter;


public class FileExtensionFilter extends javax.swing.filechooser.FileFilter implements FileFilter {
  private String[] myExtensionList = null;
  private boolean isAcceptDirectory = false;
  
  public FileExtensionFilter(String[] anExtensionlist){
    this(anExtensionlist, false);
  }
  
  public FileExtensionFilter(String[] anExtensionList, boolean acceptDirectory){
    myExtensionList = anExtensionList;
    isAcceptDirectory = acceptDirectory;
  }

  public boolean accept(File aFile) {
    if(aFile.isDirectory()){
      return isAcceptDirectory;
    }
    
    String theFileName = aFile.getName();
    for(int i=0;i<myExtensionList.length;i++){
      if(theFileName.endsWith(myExtensionList[i])) return true;
    }
    return false;
  }

  public String getDescription() {
    String theString = "";
    for(int i=0;i<myExtensionList.length;i++){
      theString += myExtensionList[i] + " ";
    }
    return theString;
  }

}
