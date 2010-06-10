/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.light;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.SwingUtilities;

import chabernac.GUI.WrappingFlowLayout;
import chabernac.chat.gui.ChatMediator;
import chabernac.chat.gui.UserListPanel;

public class UserListPanelLight extends UserListPanel {

	public UserListPanelLight(ChatMediator aMediator) {
		super(aMediator);
	}

	protected Container createAndPlaceUserPanel() {
		setLayout(new WrappingFlowLayout());
		return this; 
	}
  
  protected boolean insertComponentAfter(Point aPoint){
    Component theComponent = SwingUtilities.getDeepestComponentAt(this, myDragPoint.x, myDragPoint.y);
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
  
  public void paint(Graphics g){
    super.paint(g);
    if(myDragPoint != null){
      g.setColor(Color.GRAY);
      Component theComponent = SwingUtilities.getDeepestComponentAt(this, myDragPoint.x, myDragPoint.y);
      if(theComponent != null){
        int theLineXPosition;
        if(insertComponentAfter(myDragPoint)){
          theLineXPosition = theComponent.getX() + theComponent.getWidth();
        } else {
          theLineXPosition = theComponent.getX();
        }
        g.drawLine(theLineXPosition, theComponent.getY(), theLineXPosition, theComponent.getY() + theComponent.getHeight());
      }
    }
  }
}
