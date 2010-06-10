package chabernac.command;

import java.io.File;

import javax.swing.JFileChooser;

import chabernac.io.ExtensionFileFilter;
import chabernac.io.IOManager;

public abstract class LoadCommand implements ButtonCommand {
  private IOManager myManager = null;
  private boolean isLoadFrom = false;
  
  public LoadCommand(IOManager aManager, boolean loadFrom){
    myManager = aManager;
    isLoadFrom = loadFrom;
  }
  
  public final void execute(){
    if(!isLoadFrom) load(myManager.reload());
    else{
      JFileChooser chooser = new JFileChooser();
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("txt");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
             File theFile = chooser.getSelectedFile();
             load(myManager.loadFrom(theFile));
      }
    }
  }
  
  protected abstract void load(Object anObject);
}
