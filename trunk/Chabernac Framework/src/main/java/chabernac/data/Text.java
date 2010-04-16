package chabernac.data;
import java.awt.*;

public class Text{
  private String myText = null;
  private Font myFont = null;

  public Text(String aText, Font aFont){
    myText = aText;
    myFont = aFont;
  }
  
  public String getText(){
    return myText;
  }
  
  public Font getFont(){
    return myFont;
  }
  
  public void setText(String aText){
    myText = aText;
  }
  
  public void setFont(Font aFont){
    myFont = aFont;
  }
}