package chabernac.documentationtool;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import chabernac.command.CommandSession;
import chabernac.documentationtool.command.OpenArtifactCommand;

public class LinkListener implements MouseListener, MouseMotionListener {
  private final static Logger LOGGER = Logger.getLogger(LinkListener.class);
  
  private final DocumentationToolMediator myMediator;
  private final DocumentationArea myDocumentationArea;
  private final LinkHelper myLinkHelper;
  
  private final static Cursor HAND = new Cursor(Cursor.HAND_CURSOR);
  private final static Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

  public LinkListener(DocumentationToolMediator anMediator, DocumentationArea anDocumentationArea) {
    myMediator = anMediator;
    myDocumentationArea = anDocumentationArea;
    myLinkHelper = new LinkHelper(anDocumentationArea.getDocument());
  }

  @Override
  public void mouseClicked(MouseEvent anEvent) {
    int thePostion = myDocumentationArea.viewToModel(anEvent.getPoint());
    if(myLinkHelper.isWithinLink(thePostion)){
      try {
        String theLink = myLinkHelper.getLinkAt(thePostion);
        CommandSession.getInstance().execute( new OpenArtifactCommand( myMediator, new File(theLink) ));
      } catch (BadLocationException e) {
        LOGGER.error("Bad location", e);
      }
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent anE) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent anE) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mousePressed(MouseEvent anE) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseReleased(MouseEvent anE) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseDragged(MouseEvent anE) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mouseMoved(MouseEvent anEvent) {
    if(myLinkHelper.isWithinLink(myDocumentationArea.viewToModel(anEvent.getPoint()))){
      myDocumentationArea.setCursor(HAND);
    } else {
      myDocumentationArea.setCursor(DEFAULT);
    }
  }

}
