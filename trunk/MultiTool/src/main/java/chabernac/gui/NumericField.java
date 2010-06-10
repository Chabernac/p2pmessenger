package chabernac.gui;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class NumericField extends JTextField {
  public NumericField(){
    super();
  }
  
  public NumericField(int cols){
    super(cols);
  }
  
  protected Document createDefaultModel() {
     return new NumericDocument();
 }
  
  public int getInt(){
    return Integer.parseInt(getText());
  }
  
  public double getDouble(){
    return Double.parseDouble(getText());
  }
  
  public void setInt(int anInt){
    setText(Integer.toString(anInt));
  }

  public void setDouble(double aDouble){
    setText(Double.toString(aDouble));
  }
}
