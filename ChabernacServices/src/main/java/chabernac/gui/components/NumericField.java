package chabernac.gui.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NumericField extends JTextField{
		 private int myLow;
		 private int myHigh;
		 private boolean isLimited = false;

		 public NumericField () {
			 super();
		 }

	     public NumericField (int cols) {
	         super(cols);
	     }

	     public NumericField(int low, int high){
			 super(((int)(Math.log(high) / Math.log(10))) + 1);
			 myLow = low;
			 myHigh = high;
			 isLimited = true;
			 setText("0");
		 }

	     protected Document createDefaultModel() {
	 	      return new NumericCaseDocument();
	     }

	     class NumericCaseDocument extends PlainDocument {

	         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

	 	          if (str == null) {
	 		      return;
	 	          }
	 	          char[] chars = str.toCharArray();
	 	          for (int i = 0; i < chars.length; i++) {
					  if(chars[i] < '0' ||  chars[i] > '9') return;
	 	          }
	 	          String theString = getText(0, getLength());
	 	          super.insertString(offs, new String(chars), a);
	 	          if(isLimited){
					  int theNumber = getNumber();
					  if(theNumber < myLow || theNumber > myHigh){
						  remove(0,getLength());
						  super.insertString(0, theString, a);
					  }
				  }
	 	      }
	     }

	     public int getNumber(){
			 if(getText().equals("")) return 0;
			 return Integer.parseInt(getText());
		 }

		 public void setNumber(int aNumber){
			 setText(Integer.toString(aNumber));
		 }


}