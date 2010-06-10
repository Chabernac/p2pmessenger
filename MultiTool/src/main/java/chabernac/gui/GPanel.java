package chabernac.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GPanel extends JPanel{
  private ArrayList globalMouseListeners = null;
  private ArrayList globalMouseMotionListeners = null;
  
  private ContainerListener myContainerListener = null;

  public GPanel() {
    this(true);
  }

  public GPanel(boolean isDoubleBuffered) {
    this(new FlowLayout(), isDoubleBuffered);
  }

  public GPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    init();
  }

  public GPanel(LayoutManager layout) {
    this(layout, true);
  }
  
  private void init(){
    myContainerListener = new MyContainerListener();
  }
  
  public void addGlobalMouseListener(MouseListener aListener){
    if(globalMouseListeners == null) globalMouseListeners = new ArrayList();
    globalMouseListeners.add(aListener);
    addListenersToComponent(this, true);
  }
  
  public void addGlobalMouseMotionListener(MouseMotionListener aListener){
    if(globalMouseMotionListeners == null) globalMouseMotionListeners = new ArrayList();
    globalMouseMotionListeners.add(aListener);
    addListenersToComponent(this, true);
  }
  
  public void removeGlobalMouseListener(MouseListener aListener){
    if(globalMouseListeners == null || globalMouseListeners.isEmpty()) return;
    addListenersToComponent(this, false);
    globalMouseListeners.remove(aListener);
    addListenersToComponent(this, true);
  }
  
  public void removeGlobalMouseMotionListener(MouseMotionListener aListener){
    if(globalMouseMotionListeners == null || globalMouseMotionListeners.isEmpty()) return;
    addListenersToComponent(this, false);
    globalMouseMotionListeners.remove(aListener);
    addListenersToComponent(this, true);
  }
  
  private void addListenersToComponent(Component aComponent, boolean add){
    if(globalMouseListeners != null){
      for(int i=0;i<globalMouseListeners.size();i++){
        MouseListener theListener = (MouseListener)globalMouseListeners.get(i); 
        aComponent.removeMouseListener(theListener);
        if(add) aComponent.addMouseListener(theListener);
      }
    }
    if(globalMouseMotionListeners != null){
      for(int i=0;i<globalMouseMotionListeners.size();i++){
        MouseMotionListener theListener = (MouseMotionListener)globalMouseMotionListeners.get(i); 
        aComponent.removeMouseMotionListener(theListener);
        if(add) aComponent.addMouseMotionListener(theListener);
      }
    }
    if(aComponent instanceof Container){
      Container theContainer = ((Container)aComponent);
      theContainer.removeContainerListener(myContainerListener);
      theContainer.addContainerListener(myContainerListener);
      Component[] theComponents = ((Container)aComponent).getComponents();
      for(int i=0;i<theComponents.length;i++){
        addListenersToComponent(theComponents[i], add);
      }
    }
  }

  private class MyContainerListener implements ContainerListener{
    public void componentAdded(ContainerEvent e) {
      addListenersToComponent(e.getComponent(), true);
    }
  
    public void componentRemoved(ContainerEvent e) {
      addListenersToComponent(e.getComponent(), false);
    }
  }
  
}
