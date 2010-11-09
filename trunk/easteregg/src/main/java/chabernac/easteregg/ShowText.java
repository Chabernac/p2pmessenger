package chabernac.easteregg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;

public class ShowText extends DefaultEasterEggPaintable{
  private Color myBackgroundColor = new Color(0x000000);
  private Color myForegroundColor = new Color(0xB0FFD5);
  private double mySpeed = 0.1;
  private double myCurrentLetter = 0;
  private int x = 20;
  private int y = 20;
  private Random myRandom = null;
  private Font myFont = new Font("Courier", Font.PLAIN, 12);
  private boolean isDisplayCursor = true;
  private int myCurrentFrame = 0;
  private boolean isPaused = true;
  private int myPauseCounter = 50;
  private ArrayList myTexts = new ArrayList();
  private String myText = null;
  private int myCurrentText = 0;

  public ShowText(JFrame aRootFrame) {
    super(aRootFrame);
    myRandom = new Random();
  }

  public void paint(Graphics aGraphics, Rectangle aBounds, BufferedImage aBackGround) {
    if(myTexts.size() == 0){
      myTexts.add("This software is cool.");
    }
    
    if(myText == null){
      myText = (String)myTexts.get(0);
    }
    
    if(!isPaintBackground()){
      aGraphics.setColor(myBackgroundColor);
      aGraphics.fillRect(0, 0, (int)aBounds.getWidth(), (int)aBounds.getHeight());
    } else {
      aGraphics.drawImage(aBackGround, 0, 0, null);
    }
    
    myCurrentFrame++;
    
    if(!isPaused && myRandom.nextInt() % 30 == 0){
      isPaused = true;
      myPauseCounter = Math.abs(myRandom.nextInt() % 50);
    } else {
      myPauseCounter--;
      if(myPauseCounter < 0){
        isPaused = false;
      }
    }

    if(!isPaused){
      myCurrentLetter += mySpeed + (myRandom.nextDouble() - 0.5) * mySpeed;
    }
    
    int theCurrentLetter = (int)Math.floor(myCurrentLetter);
    
    if(theCurrentLetter > myText.length()){
      myCurrentLetter = 0;
      theCurrentLetter = 0;
      myCurrentText += 1;
      myCurrentText = myCurrentText % myTexts.size();
      myText = (String)myTexts.get(myCurrentText);
    }
    
    String theText = myText.substring(0, theCurrentLetter);
    
    
    if(myCurrentFrame % 20 == 0){
      isDisplayCursor = !isDisplayCursor;
    }
    
    if(!isPaused || isDisplayCursor){
      theText += "_";
    }
    
    aGraphics.setColor(myForegroundColor);
    aGraphics.setFont(myFont);
    aGraphics.drawString(theText, x, y);
    
    if(!isPaused && theCurrentLetter == myText.length()){
      isPaused = true;
      myPauseCounter = 30;
    }
  }
  
  
  
  public String getText() {
    return myText;
  }

  public void setText(String aText) {
    myTexts.add(aText.replaceAll("_", " "));
  }
  

  public Color getBackgroundColor() {
    return myBackgroundColor;
  }

  public void setBackgroundColor(Color anBackgroundColor) {
    myBackgroundColor = anBackgroundColor;
  }

  public Color getForegroundColor() {
    return myForegroundColor;
  }

  public void setForegroundColor(Color anForegroundColor) {
    myForegroundColor = anForegroundColor;
  }
  
  public double getSpeed() {
    return mySpeed;
  }

  public void setSpeed(double anSpeed) {
    mySpeed = anSpeed;
  }
  
  

  public int getX() {
    return x;
  }

  public void setX(int anX) {
    x = anX;
  }

  public int getY() {
    return y;
  }

  public void setY(int anY) {
    y = anY;
  }

  public void setParameter(Object aParameter){
    super.setParameter(aParameter);
    if(aParameter instanceof String){
      String theParam = (String)aParameter;
      StringTokenizer theTokenizer = new StringTokenizer(theParam, "=");
      if(theTokenizer.countTokens() == 2){
        String theKey = theTokenizer.nextToken();
        String theValue = theTokenizer.nextToken();
        if(theKey.equals("text")){
          setText(theValue);
        }
        if(theKey.equals("background")){
          setBackgroundColor(new Color(Integer.parseInt(theValue)));
        }
        if(theKey.equals("foreground")){
          setForegroundColor(new Color(Integer.parseInt(theValue)));
        }
        if(theKey.equals("speed")){
          setSpeed(Double.parseDouble(theValue));
        }
        if(theKey.equals("x")){
          setX(Integer.parseInt(theValue));
        }
        if(theKey.equals("y")){
          setY(Integer.parseInt(theValue));
        }
        if(theKey.equals("font")){
          StringTokenizer theFontTokenizer = new StringTokenizer(theValue, ".");
          if(theFontTokenizer.countTokens() == 3){
            myFont = new Font(theFontTokenizer.nextToken(), Integer.parseInt(theFontTokenizer.nextToken()), Integer.parseInt(theFontTokenizer.nextToken())); 
          }
        }
      }
    }
  }



}
