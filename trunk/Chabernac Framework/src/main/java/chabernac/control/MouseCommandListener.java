/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.control;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.*;

/**
 *
 *
 * @version v1.0.0      1-jul-2004
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 1-jul-2004 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class MouseCommandListener implements MouseListener, MouseMotionListener, MouseWheelListener {
  private MouseCommand myMouseCommand = null;
  private Component myComponent = null;
  private Robot myRobot = null;
  private SynchronizedEventManagerOld myManager = null;
  private int myCenterX, myCenterY;
  private long lastMouseTime;
  
  public MouseCommandListener(Component aComponent, MouseCommand aMouseCommand, SynchronizedEventManagerOld aManager) throws AWTException{
    myMouseCommand = aMouseCommand;
    myComponent = aComponent;
    myCenterX = myComponent.getWidth() / 2;
    myCenterY = myComponent.getHeight() / 2;
    myRobot = new Robot();
    myManager = aManager;
    aComponent.addMouseListener(this);
    aComponent.addMouseMotionListener(this);
    aComponent.addMouseWheelListener(this);
  }
  

  public void mousePressed(MouseEvent e) {
    int theButton = e.getButton();
    switch(theButton){
      case MouseEvent.BUTTON1:{
        myMouseCommand.mouseLeftClicked();
        break;
      }
      case MouseEvent.BUTTON2:{
        myMouseCommand.mouseMidClicked();
        break;
      }
      case MouseEvent.BUTTON3:{
        myMouseCommand.mouseRightClicked();
        break;
      }
    }
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseDragged(MouseEvent e) {}

  public synchronized void mouseMoved(MouseEvent e) {
    long currentTime = e.getWhen();
    long interval = currentTime - lastMouseTime;
    if(interval < 100) return;
    lastMouseTime = currentTime;
    
    //System.out.println("diffx: "  + (e.getX() - myCenterX));  

    float xSpeed =  ((float)((e.getX() - myCenterX) * 1000))  / (float)interval;
    float ySpeed =  ((float)((e.getY() - myCenterY) * 1000))  / (float)interval;
    //System.out.println("X speed (pix/sec): " + (int)xSpeed + " Y speed (pix/sec): "  + (int)ySpeed);
    //mySynchronizedTimer.waitForSynch();
    myMouseCommand.mouseMoved(xSpeed, ySpeed);
    myRobot.mouseMove(myCenterX, myCenterY);
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    int theRotation = e.getWheelRotation();
    if(theRotation < 0) myMouseCommand.mouseScrollUp();
    else if(theRotation > 0) myMouseCommand.mouseScrollDown();
  }

}
