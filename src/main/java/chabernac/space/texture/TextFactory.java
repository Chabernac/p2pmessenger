package chabernac.space.texture;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Map;

public class TextFactory {
  private static int LETTER_WIDTH = 5;
  private static int LETTER_HEIGHT = 8;
  private static int lETTER_SIZE = LETTER_HEIGHT * LETTER_WIDTH;
  private static Color COLOR = Color.black;
  private static Font FONT = new Font("Arial", Font.PLAIN, 10);
  
  private static Map LETTER_MAP = new HashMap();
  
  public int[] getText(String aText, Graphics g){
    FontMetrics theMetrics = g.getFontMetrics(FONT);
    int theWidth = theMetrics.stringWidth(aText);
    int theHeight = theMetrics.getHeight();
    BufferedImage theCharImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics theGraphics = theCharImage.getGraphics();
    theGraphics.setColor(new Color(0,0,0,0));
    theGraphics.clearRect(0, 0, theWidth, theHeight);
    theGraphics.drawString(aText, 0, 0);
    return ((DataBufferInt)theCharImage.getData().getDataBuffer()).getData();
    /*
    int[] theColors = new 
    char[] theChars = aText.toCharArray();
    for(int i=0;i<theChars.length;i++){
      char theChar = theChars[i];
      int[] theCharColors = getChar(theChar, g);
    }
    */
  }
  
  public static int[] getChar(char aChar, Graphics g){
    Character theChar = new Character(aChar);
    if(!LETTER_MAP.containsKey(theChar)){
      FontMetrics theMetrics = g.getFontMetrics(FONT);
      int theWidth = theMetrics.charWidth(aChar);
      int theHeight = theMetrics.getHeight();
      BufferedImage theCharImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics theGraphics = theCharImage.getGraphics();
      theGraphics.setColor(new Color(0,0,0,0));
      theGraphics.clearRect(0, 0, theWidth, theHeight);
      theGraphics.drawChars(new char[]{aChar}, 0, 1, 0, 0);
      int[] theDataBuffer = ((DataBufferInt)theCharImage.getData().getDataBuffer()).getData();
      LETTER_MAP.put(theChar, theDataBuffer);
    }
    return (int[])LETTER_MAP.get(theChar);
  }

}
