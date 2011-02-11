package chabernac.documentationtool;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.Artifact.Attribute;

public class DocumentationFrame extends JFrame {
  private static final long serialVersionUID = 1866073499445428924L;
  private JTabbedPane myTabs = new JTabbedPane();
  private final DocumentationToolMediator myMediator;
  
  private Map<UUID, ArtifactPanel> myPanels = new HashMap<UUID, ArtifactPanel>();
  
  public DocumentationFrame(DocumentationToolMediator aMediator){
    myMediator = aMediator;
    setSize(800, 600);
    setVisible( true );
    buildGUI();
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    add(myTabs, BorderLayout.CENTER);
    addEmptyTab();
  }
  
  private void addEmptyTab(){
    openArtifact( Artifact.getDocumentInstance() );
  }
  
  public void openArtifact(File aFile) throws FileNotFoundException, IOException, ClassNotFoundException{
    Artifact theArtifact = Artifact.loadFromStream( new FileInputStream( aFile ) );
    openArtifact(theArtifact);
  }

  public void openArtifact( Artifact anArtifact ) {
    if(!myPanels.containsKey( anArtifact.getArtifactId() )){
      ArtifactPanel thePanel = new ArtifactPanel( myMediator, anArtifact );
      myTabs.add(anArtifact.getAttribute( Attribute.Name), thePanel);
      thePanel.setDividerLocation();  
      myPanels.put( anArtifact.getArtifactId(), thePanel );
    }
    
    myTabs.setSelectedComponent( myPanels.get( anArtifact.getArtifactId() ) );
  }
  
  public Artifact getCurrentArtifact(){
    return ((ArtifactPanel)myTabs.getSelectedComponent()).getArtifact();
  }

  public void setTabName( Artifact anArtifact ) {
    myTabs.setTitleAt( myTabs.indexOfComponent( myPanels.get( anArtifact.getArtifactId() )), anArtifact.getAttribute( Attribute.Name ) );
  }
  
}
