package chabernac.easteregg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class Matrix extends DefaultEasterEggPaintable {
  private static Logger LOGGER = Logger.getLogger(Matrix.class);


  public static char[] CHARS = null;
  public static char[] ALPHABET_CHARS = null;
  public static char[] NUMERIC_CHARS = null;
  public static char[] ALPHANUMERIC_CHARS = null;
  private static final int SLOT_WIDTH = 12;
  private static final int SLOT_HEIGHT = 12;

  private Rectangle myBounds = null;
  private Random myRandom = null;
  private static Color GREEN_PREVIOUS = new Color(0x0AB319);
  private static Color GREEN_LAST = new Color(0xB0FFD5);
  private static Font PREVIOUS_LETTER_FONT = new Font("Arial", Font.PLAIN, 12);
  private static Font FIRST_LETTER_FONT = new Font("Arial", Font.BOLD, 12);
  private VerticalLine[] myLines = null;
  private int horizontalSlots;
  private int verticalSlots; 
  private boolean[] slotTaken;
  private String[] TEXTS = null;
  private List<String> myMessages = null;

  private double myLinesFactor = 2d;

  private boolean isShowBackGround = false;

  static{
    CHARS = new char[125 - 33 + 1];

    int x = 0;
    for(int i=33;i<=125;i++){
      CHARS[x++] = (char)i;
    }

    ALPHABET_CHARS = new char[26 * 2];
    int i=0;
    for(char theChar='a';theChar<='z';theChar++){
      ALPHABET_CHARS[i++] = theChar;
    }
    for(char theChar='A';theChar<='Z';theChar++){
      ALPHABET_CHARS[i++] = theChar;
    }
    i = 0;
    NUMERIC_CHARS = new char[10];
    for(char theChar='0';theChar<='9';theChar++){
      NUMERIC_CHARS[i++] = theChar;
    }
    ALPHANUMERIC_CHARS = concat(ALPHABET_CHARS, NUMERIC_CHARS);
  }

  public static char[] concat(char[] aCharArray, char[] anotherCharArray){
    char[] theChars = new char[aCharArray.length + anotherCharArray.length];
    System.arraycopy(aCharArray, 0, theChars, 0, aCharArray.length);
    System.arraycopy(anotherCharArray, 0, theChars, aCharArray.length, anotherCharArray.length);
    return theChars;
  }

  public Matrix(JFrame aRootFrame){
    super(aRootFrame);
    myRandom = new Random();
    myMessages = new ArrayList<String>();
  }

  private void init(){
    horizontalSlots = myBounds.width / SLOT_WIDTH;
    verticalSlots = (int)Math.ceil((double)myBounds.height / (double)SLOT_HEIGHT);
    slotTaken = new boolean[horizontalSlots];
    myLines = new VerticalLine[(int)(horizontalSlots * myLinesFactor)];
    
    TEXTS = (String[])myMessages.toArray(new String[]{});

    for(int i=0;i<myLines.length;i++){
      myLines[i] = new VerticalLine(randomSpeed());
    }
  }

  public void paint(Graphics g, Rectangle aBounds, BufferedImage aBackGround) {
    if(myBounds == null || !myBounds.equals(aBounds)){
      myBounds = aBounds;
      g.setColor(Color.black);
      //g.fillRect(0, 0, myBounds.width, myBounds.height);
      init();
    }



    //		g.setColor(Color.green);
    //		g.setFont(FONT);
    for(int i=0;i<myLines.length;i++){
      if(myLines[i] == null){
        System.out.println("Null at: " + i);
      } else {
        myLines[i].paint(g, aBounds, aBackGround);
      }
    }
  }

  private int getSlot(){
    int theSlot = 0;
    while(slotTaken[theSlot]){
      theSlot = (Math.abs(myRandom.nextInt())) % horizontalSlots;
    }
    slotTaken[theSlot] = true;
    return theSlot;
  }

  private int getRandomX(){
    return Math.abs(myRandom.nextInt() % myBounds.width);
  }

  private void freeSlot(int aSlot){
    slotTaken[aSlot] = false;
  }

  private float randomSpeed(){
    return (myRandom.nextFloat() * 5) + 1;
  }

  private String randomLetter(){
    int theChar = Math.abs(myRandom.nextInt() % ALPHANUMERIC_CHARS.length);
    return new String(new char[]{ALPHANUMERIC_CHARS[theChar]});

    //return new String(new char[]{(char)('a' + (Math.abs(myRandom.nextInt())) % 26)});
  }

  private class VerticalLine implements iPaintable{
    private float mySpeed;
    private boolean isAlive = true;
    //private int slot;
    private int x;
    private float y;
    private ArrayList myLetters = null;
    private int lastY = 0;
    int i = 0;

    public VerticalLine(float aSpeed){
      mySpeed = aSpeed;
      //slot = getSlot();
      //x = slot * SLOT_WIDTH;
      x = getRandomX();

      myLetters = new ArrayList(verticalSlots);
      while(myLetters.size() < verticalSlots){
        if(myRandom.nextInt() % 5 == 0){
          addSomeText(myLetters);
        } else {
          myLetters.add(randomLetter());
        }	  
      }
    }

    public void paint(Graphics g, Rectangle aBounds, BufferedImage aBackGround) {
      try{
        if(aBackGround != null && isPaintBackground()){
          int theX = x - 1;
          int theY = (int)y - SLOT_HEIGHT;
          if(theX < 0){
            theX = 0;
          } else if(theX  + SLOT_WIDTH + 1 >= aBackGround.getWidth()){
            theX = aBackGround.getWidth() - 2 - SLOT_WIDTH;
          }
          if(theY < 0){
            theY = 0;
          } else if(theY  + SLOT_HEIGHT>= aBackGround.getHeight()){
            theY = aBackGround.getHeight() - 1 - SLOT_HEIGHT;
          }

          BufferedImage theSample = aBackGround.getSubimage(theX, theY, SLOT_WIDTH + 1, SLOT_HEIGHT);
          g.drawImage(theSample, x - 1, (int)y - SLOT_HEIGHT, null);
        } else {
          g.setColor(Color.black);
          g.fillRect(x - 1, (int)y - SLOT_HEIGHT, SLOT_WIDTH + 1, SLOT_HEIGHT);
        }

        y += mySpeed;

        g.setColor(GREEN_LAST);
        g.setFont(FIRST_LETTER_FONT);
        g.drawString((String)myLetters.get(myLetters.size() - 1), x, (int)y);


        if( y - lastY > SLOT_HEIGHT){
          //					g.setColor(Color.black);
          //					g.fillRect(x, lastY, SLOT_WIDTH, SLOT_HEIGHT);
          g.setColor(GREEN_PREVIOUS);
          g.setFont(PREVIOUS_LETTER_FONT);	
          g.drawString((String)myLetters.get(i), x, lastY);
          i++;
          lastY += SLOT_HEIGHT;
        }



        if( lastY > myBounds.height){
          //g.setColor(Color.black);
          //g.fillRect(x, 0, SLOT_WIDTH, myBounds.height);

          //freeSlot(slot);
          //slot = getSlot();
          x = getRandomX();
          //x = slot * SLOT_WIDTH;
          y = 0;
          lastY = 0;
          i = 0;
        }
      }catch(Exception e){
        LOGGER.error("Exception: ", e);
      }

      /*
      if(Math.ceil(y / SLOT_HEIGHT) > myLetters.size()){
        if(myRandom.nextInt() % 5 == 0){
          addSomeText(myLetters);
        } else {
          myLetters.add(randomLetter());
        }
      }
       */

      /*			

			if( y - lastY > SLOT_HEIGHT){
				g.setColor(Color.black);
				g.fillRect(x, lastY, SLOT_WIDTH, SLOT_HEIGHT);
				g.setColor(GREEN_PREVIOUS);
				g.setFont(PREVIOUS_LETTER_FONT);	
				g.drawString((String)myLetters.get(i), x, lastY);
				i++;
				lastY += SLOT_HEIGHT;
			}

       */
      //int i = 0;



      /*
      for(int yCur = 0;yCur<y -SLOT_HEIGHT ; yCur += SLOT_HEIGHT){

    	  //System.out.println(i  + ": " + myLetters.size());
        g.drawString((String)myLetters.get(i++), x, yCur);
      }
       */
      /*
			g.setColor(Color.black);
			g.fillRect(x, (int)(y + SLOT_HEIGHT - mySpeed), SLOT_WIDTH, SLOT_HEIGHT);
			g.setColor(GREEN_LAST);
			g.setFont(FIRST_LETTER_FONT);
			g.drawString((String)myLetters.get(myLetters.size() - 1), x, (int)(y + SLOT_HEIGHT));
       */
      /*
			if( y > myBounds.height){
				g.setColor(Color.black);
				g.fillRect(x, 0, SLOT_WIDTH, myBounds.height);

				//freeSlot(slot);
				//slot = getSlot();
				x = getRandomX();
				//x = slot * SLOT_WIDTH;
				y = 0;
				lastY = 0;
				i = 0;
			}
       */
    }

    private void addSomeText(ArrayList aLetters) {
      int which = Math.abs(myRandom.nextInt() % TEXTS.length);
      String theText = "this software is cool"; 
      if(TEXTS[which] != null){
        theText = TEXTS[which];
      }

      for(int i=0;i<theText.length();i++){
        aLetters.add(theText.substring(i,i + 1));
      }
    }

    public boolean isAlive(){
      return isAlive;
    }
  }

  public boolean isShowBackGround() {
    return isShowBackGround;
  }

  public void setShowBackGround(boolean isShowBackGround) {
    this.isShowBackGround = isShowBackGround;
  }

  public double getLinesFactor() {
    return myLinesFactor;
  }

  public void setLinesFactor(double myLinesFactor) {
    this.myLinesFactor = myLinesFactor;
  }

  public void setParameter(Object aParameter){
    super.setParameter(aParameter);
    if(aParameter instanceof String){
      String theParam = (String)aParameter;
      StringTokenizer theTokenizer = new StringTokenizer(theParam, "=");
      if(theTokenizer.countTokens() == 2){
        String theKey = theTokenizer.nextToken();
        String theValue = theTokenizer.nextToken();
        if(theKey.equals("factor")){
          setLinesFactor(Double.parseDouble(theValue));
        }
      } else {
        myMessages.add(theParam);
      }
    }
  }
}
