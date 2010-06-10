
package chabernac.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumericDocument extends PlainDocument {
  
  public void insertString(int offs, String str, AttributeSet a)  throws BadLocationException {
    if (str == null) return;
    String theNumber = "";
    char[] theChars = str.toCharArray();
    for(int i=0;i<theChars.length;i++){
      if(theChars[i] >= '0' && theChars[i] <= '9'){
        theNumber += theChars[i];
      }
    }
    super.insertString(offs, theNumber, a);
  }

}
