package chabernac.command;

import java.io.File;

import javax.swing.JFileChooser;

import chabernac.io.ExtensionFileFilter;
import chabernac.io.IOManager;

public abstract class SaveCommand implements ButtonCommand {
  private IOManager myManager = null;
  private boolean isSaveAs = false;
  
  public SaveCommand(IOManager aManager, boolean saveAs){
    myManager = aManager;
    isSaveAs = saveAs;
  }
  
  public final void execute(){
    if(!isSaveAs) myManager.save(save());
    else{
      JFileChooser chooser = new JFileChooser();
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("txt");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
             File theFile = chooser.getSelectedFile();
             myManager.saveAs(save(), theFile);
      }
    }
  }
  
  protected abstract Object save();
}
