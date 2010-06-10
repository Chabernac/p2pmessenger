/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.heavy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chabernac.chat.gui.ChatMediator;
import chabernac.chat.gui.UserListPanel;

public class UserListPanelHeavy extends UserListPanel {

	public UserListPanelHeavy(ChatMediator aMediator) {
		super(aMediator);
	}

	protected Container createAndPlaceUserPanel() {
		setLayout(new BorderLayout());
		JPanel theUsersPanel = new JPanel(new GridLayout(-1,1));
		
		JPanel theCenterPanel = new JPanel(new BorderLayout());
		theCenterPanel.add(theUsersPanel, BorderLayout.NORTH);
		
		JScrollPane thePane = new JScrollPane(theCenterPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		thePane.getVerticalScrollBar().setUnitIncrement(10);
		add(thePane, BorderLayout.CENTER);
		return theUsersPanel;

	}
  
  protected boolean insertComponentAfter(Point aPoint){
    Component theComponent = SwingUtilities.getDeepestComponentAt(this, myDragPoint.x, myDragPoint.y);
    if(theComponent != null){
      int theComponentHalfHeightPostion = theComponent.getY() + theComponent.getHeight() / 2;
      if(myDragPoint.y < theComponentHalfHeightPostion){
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
        int theLineYPosition;
        if(insertComponentAfter(myDragPoint)){
          theLineYPosition = theComponent.getY() + theComponent.getHeight();
        } else {
          theLineYPosition = theComponent.getY();
        }
        g.drawLine(theComponent.getX(), theLineYPosition, theComponent.getX() + theComponent.getWidth(), theLineYPosition);
      }
    }
  }

}
