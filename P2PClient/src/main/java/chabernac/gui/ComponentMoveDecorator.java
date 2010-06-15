/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;


public class ComponentMoveDecorator {
  private final GPanel myGPanel;
  private final iComponentMoveListener myComponenentMoveListener;
  private Point myDragPoint = null;

  public ComponentMoveDecorator(GPanel aPanel, iComponentMoveListener aListener){
    myGPanel = aPanel;
    myComponenentMoveListener = aListener;

    ComponentDragListener theDragListener = new ComponentDragListener();
    aPanel.addGlobalMouseListener(theDragListener);
    aPanel.addGlobalMouseMotionListener(theDragListener);
  }

  private class ComponentDragListener implements MouseListener, MouseMotionListener{

    public Point getRelativePoint(MouseEvent evt){
      return SwingUtilities.convertPoint(evt.getComponent(), evt.getX(), evt.getY(), myGPanel);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
      Point theRelativePoint = getRelativePoint(e);
      Component theSourceComponent = e.getComponent();
      Component theTargetComponent = SwingUtilities.getDeepestComponentAt(myGPanel, theRelativePoint.x, theRelativePoint.y);

      if(!(theSourceComponent == null || 
          theTargetComponent == null || 
          !(theSourceComponent instanceof Component) ||
          !(theTargetComponent instanceof Component) ||
          theSourceComponent == theTargetComponent)){
        boolean isInsertAfter = insertComponentAfter( theRelativePoint );
        myComponenentMoveListener.componentDropped( theTargetComponent, theSourceComponent, isInsertAfter );
      }
      myDragPoint = null;
      myComponenentMoveListener.removeSeparator();
      myGPanel.repaint();
    }

    public void mouseDragged(MouseEvent e) {
      myDragPoint = getRelativePoint(e);
      if(myDragPoint != null){
        Component theComponent = SwingUtilities.getDeepestComponentAt(myGPanel, myDragPoint.x, myDragPoint.y);
        if(theComponent != null){
          myComponenentMoveListener.drawSeperator(theComponent, insertComponentAfter(myDragPoint));
        }
      }
    }

    public void mouseMoved(MouseEvent e) {
//      myLatestMouseLocation = getRelativePoint(e);
    }

  }

  protected boolean insertComponentAfter(Point aPoint){
    Component theComponent = SwingUtilities.getDeepestComponentAt(myGPanel, myDragPoint.x, myDragPoint.y);
    if(theComponent != null){
      int theComponentHalfHeightPostion = theComponent.getX() + theComponent.getWidth() / 2;
      if(myDragPoint.x < theComponentHalfHeightPostion){
        return false;
      } else {
        return true;
      }
    }
    return true;
  }
}
