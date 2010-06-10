/*
 * Created on 13-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class CPanel extends JPanel {
  private static Logger logger = Logger.getLogger(CPanel.class);
	private JFrame myRootFrame = null;
	private Component myCurrentComponent = null;
	private MyHierarchyListener myHierarchyListener = null;
	private boolean isEnabled = true;

	public CPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public CPanel(LayoutManager layout) {
		super(layout);
	}

	public CPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public CPanel() {
		super();
	}
  
	private void findRoot(){
		if(myCurrentComponent == null) myCurrentComponent = this;
		if(myCurrentComponent.getParent() == null){
			if(myHierarchyListener == null) myHierarchyListener = new MyHierarchyListener();
			myCurrentComponent.addHierarchyListener(myHierarchyListener);
		} else {
			myCurrentComponent.removeHierarchyListener(myHierarchyListener);
			myCurrentComponent = myCurrentComponent.getParent();
			if(myCurrentComponent instanceof JFrame){
				myRootFrame = (JFrame)myCurrentComponent;
				rootFound();
			} else {
				findRoot();
			}
		}
	}
	
	private void rootFound(){
    setGlassPane(true);
    MyMouseListener theListener = new MyMouseListener();
		myRootFrame.getGlassPane().addMouseListener(theListener);
    //myRootFrame.getGlassPane().addMouseMotionListener(theListener);
	}
	
	private class MyHierarchyListener implements HierarchyListener{

		public void hierarchyChanged(HierarchyEvent e) {
			findRoot();
		}
	}
	
	private class MyMouseListener implements MouseListener, MouseMotionListener{

		public void mouseClicked(MouseEvent e) {
			redispatch(e);
		}

		public void mousePressed(MouseEvent e) {
			redispatch(e);
		}

		public void mouseReleased(MouseEvent e) {
			redispatch(e);
		}

		public void mouseEntered(MouseEvent e) {
			redispatch(e);
		}

		public void mouseExited(MouseEvent e) {
			redispatch(e);
		}
		
		
		private void redispatch(MouseEvent e){
      
			setGlassPane(false);
			Point thePoint = SwingUtilities.convertPoint(myRootFrame.getGlassPane(), e.getX(), e.getY(), myRootFrame);
			Component theComponent = SwingUtilities.getDeepestComponentAt(myRootFrame, thePoint.x, thePoint.y);
      
      MouseEvent theEvt = SwingUtilities.convertMouseEvent(myRootFrame.getGlassPane(), e, theComponent);
      logger.debug("Redispatching mouse event to component: " + theComponent.getClass() + e.isConsumed());
			theComponent.dispatchEvent(theEvt);
			
			Point theLocalPoint = SwingUtilities.convertPoint(myRootFrame.getGlassPane(), e.getX(), e.getY(), CPanel.this);
			if(contains(theLocalPoint)) dispatchEvent(SwingUtilities.convertMouseEvent(myRootFrame.getGlassPane(), e, CPanel.this));
			
			setGlassPane(true);
		}

    public void mouseDragged(MouseEvent e) {
      redispatch(e);
    }

    public void mouseMoved(MouseEvent e) {
      redispatch(e);
    }
		
	}
	
	public void setGlobalListenerEnabled(boolean enable){
    if(enable && myRootFrame == null) findRoot();
		isEnabled = enable;
		setGlassPane(isEnabled);
	}
	
	private void setGlassPane(boolean visible){
    if(myRootFrame != null){
  		if(isEnabled) myRootFrame.getGlassPane().setVisible(visible);
  		else myRootFrame.getGlassPane().setVisible(false);
    }
	}
	
	public JFrame getRootFrame(){
		return myRootFrame;
	}
}
