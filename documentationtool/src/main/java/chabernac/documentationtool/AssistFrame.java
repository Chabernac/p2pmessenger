package chabernac.documentationtool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.DocumentationBase;



public class AssistFrame implements iDocumentAssistant{

  @Override
  public void assist(Artifact<Document> anArtifact, int aCursorPosition) {
    Set<DocumentationBase> theBases = anArtifact.getDocumentationBase();

    List<File> theFiles = searchFiles( theBases );

    File theFile = chooseFile(theFiles);

    try {
      anArtifact.getContent().insertString(aCursorPosition, "[" + theFile.getAbsolutePath() + "]", null );
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  private File chooseFile(List<File> aFiles){
    JList theList = new JList( aFiles.toArray() );
    JOptionPane.showConfirmDialog( null, theList, "select file", JOptionPane.OK_OPTION );
    File theSelectedFile = (File)theList.getSelectedValue();
    return theSelectedFile;
  }

  private List<File> searchFiles(Set<DocumentationBase> aBases){

    List<File> theFiles = new ArrayList<File>();
    Queue<File> theQueue = new LinkedBlockingQueue<File>();

    for(DocumentationBase theBase : aBases){
      theQueue.add( theBase.getLocation() );
    }

    while(!theQueue.isEmpty()){
      File theDirectory = theQueue.poll();

      File[] theSubFiles = theDirectory.listFiles();
      for(File theFile : theSubFiles){

        if(theFile.isDirectory()){
          theQueue.add(theFile);
        } else if(theFile.getName().endsWith( ".docj" )){
          theFiles.add(theFile);
        } 
      }
    }
    return theFiles;
  }

}
