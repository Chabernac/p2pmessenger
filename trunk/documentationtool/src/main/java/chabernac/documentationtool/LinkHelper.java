package chabernac.documentationtool;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

public class LinkHelper {
  private static final Logger LOGGER = Logger.getLogger(LinkHelper.class);

  private final static int MAX_LINK_LENGTH = 128;
  private final static int HALF_LINK_LENGTH = MAX_LINK_LENGTH / 2;


  private final Document aDocument;

  public LinkHelper(Document anDocument) {
    super();
    aDocument = anDocument;
  }
  
  private int getMinLinkLocation(int aPosition) throws BadLocationException{
    if(aPosition >= aDocument.getLength()) throw new BadLocationException("The specified postion is out of the text range", aPosition);
    int theMinPostion = aPosition - HALF_LINK_LENGTH;
    if(theMinPostion < 0) theMinPostion = 0;
    
    return theMinPostion;
  }
  
  private int getMaxLinkLocation(int aPosition) throws BadLocationException{
    int theMaxPostion = aPosition + HALF_LINK_LENGTH;
    if(theMaxPostion >= aDocument.getLength()) theMaxPostion = aDocument.getLength();
    return theMaxPostion;
  }
  
  private String getStringAround(int aPosition) throws BadLocationException{
    int theMinPostion = getMinLinkLocation(aPosition);
    int theMaxPostion = getMaxLinkLocation(aPosition);
    return aDocument.getText(theMinPostion, theMaxPostion - theMinPostion);
  }
  public boolean isWithinLink(int aPosition){
    
    try{
      int theMinPostion = getMinLinkLocation(aPosition);
      int theMaxPostion = getMaxLinkLocation(aPosition);
      String theText = aDocument.getText(theMinPostion, theMaxPostion - theMinPostion);
    
      for(int i=aPosition;i>=theMinPostion;i--){
        if(theText.charAt(i) == ']') return false;
        else if(theText.charAt(i) == '[') return true;
      }
    }catch(Exception e){
//      LOGGER.error("Bad location", e);
      return false;
    }

    return false;
  }

  public String getLinkAt(int aPosition) throws BadLocationException {
    int theMinPostion = getMinLinkLocation(aPosition);
    int theMaxPostion = getMaxLinkLocation(aPosition);
    String theText = aDocument.getText(theMinPostion, theMaxPostion - theMinPostion);
    
    int theStart = -1;
    for(int i=aPosition;i>=theMinPostion;i--){
      if(theText.charAt(i) == ']') throw new BadLocationException("Postion not within link", aPosition);
      else if(theText.charAt(i) == '[') { 
        theStart = i + 1;
        break;
      }
    }
    
    int theEnd = -1;
    for(int i=aPosition;i<=theMaxPostion;i++){
      if(theText.charAt(i) == '[') throw new BadLocationException("Postion not within link", aPosition);
      else if(theText.charAt(i) == ']') { 
        theEnd = i;
        break;
      }
    }
    
    if(theStart == -1 || theEnd == -1) throw new BadLocationException("Link not found", aPosition);
    
    return aDocument.getText(theStart, theEnd - theStart);
  }


  


}
