/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class GPanelPopupMenu extends JPopupMenu{
	private GPanel myPanel = null;
	
	public GPanelPopupMenu(GPanel aPanel){
		myPanel = aPanel;
		myPanel.addGlobalMouseListener(new MyMouseAdpater());
		setInvoker(aPanel);
	}

	private class MyMouseAdpater extends MouseAdapter{
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3){
				Point theRelativePoint = SwingUtilities.convertPoint(e.getComponent(), e.getX(), e.getY(), myPanel);
			    show(myPanel, theRelativePoint.x, theRelativePoint.y);
			}
		}
	}
}
