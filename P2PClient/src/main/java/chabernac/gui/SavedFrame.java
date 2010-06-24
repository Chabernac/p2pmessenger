/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Properties;

import javax.swing.JFrame;

import chabernac.preference.ApplicationPreferences;

public abstract class SavedFrame extends JFrame{
  private static final long serialVersionUID = 6361226538701714775L;
  
  private Rectangle myRectangle = null;

  public SavedFrame(String title, Rectangle aRectangle) throws HeadlessException {
    super(title);
    myRectangle = aRectangle;
    init();
    addListeners();
  }

  private void addListeners() {
    addComponentListener(  new MyComponentListener() );
  }

  private void init(){
    loadProperties();
  }


  private void saveProperties(){
    Properties theProperties = ApplicationPreferences.getInstance();
    theProperties.setProperty("frame." + getFrameName() +  ".size.width", Integer.toString(getWidth()));
    theProperties.setProperty("frame." + getFrameName() + ".size.height", Integer.toString(getHeight()));
    theProperties.setProperty("frame." + getFrameName() + ".location.x", Integer.toString(getLocation().x));
    theProperties.setProperty("frame." + getFrameName() + ".location.y", Integer.toString(getLocation().y));
  }

  private void loadProperties(){
    Properties theProperties = ApplicationPreferences.getInstance();
    if(theProperties.containsKey("frame." + getFrameName() + ".size.width")) myRectangle.width = Integer.parseInt(theProperties.getProperty("frame." + getFrameName() + ".size.width")); 
    if(theProperties.containsKey("frame." + getFrameName() + ".size.height")) myRectangle.height = Integer.parseInt(theProperties.getProperty("frame." + getFrameName() + ".size.height"));
    setSize(myRectangle.width, myRectangle.height);

    if(theProperties.containsKey("frame." + getFrameName() + ".location.x")) myRectangle.x = Integer.parseInt(theProperties.getProperty("frame." + getFrameName() + ".location.x")); 
    if(theProperties.containsKey("frame." + getFrameName() + ".location.y")) myRectangle.y = Integer.parseInt(theProperties.getProperty("frame." + getFrameName() + ".location.y"));
    setLocation(myRectangle.x, myRectangle.y);
  }

  protected abstract String getFrameName();
  
  public class MyComponentListener extends ComponentAdapter {
    @Override
    public void componentMoved( ComponentEvent anE ) {
      saveProperties();
    }
    @Override
    public void componentResized( ComponentEvent anE ) {
      saveProperties();
    }
  }
}
