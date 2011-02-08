package chabernac.documentationtool;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class DocumentationFrame extends JFrame {
  private static final long serialVersionUID = 1866073499445428924L;
  private JTabbedPane myTabs = new JTabbedPane();
  private final DocumentationToolMediator myMediator;
  
  public DocumentationFrame(DocumentationToolMediator aMediator){
    myMediator = aMediator;
    buildGUI();
  }
  
  private void buildGUI(){
    addEmptyTab();
    setLayout(new BorderLayout());
    add(myTabs, BorderLayout.CENTER);
  }
  
  private void addEmptyTab(){
    myTabs.add("new document", new JScrollPane(new DocumentationArea(myMediator)));
  }
}
