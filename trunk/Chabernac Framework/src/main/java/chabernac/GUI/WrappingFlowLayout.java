/*
 * Created on 11-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class WrappingFlowLayout implements LayoutManager {
  private int myHgap, myVgap;
  
  public WrappingFlowLayout(){
    this(2, 2);
  }
  
  public WrappingFlowLayout(int hgap, int vgap){
    myHgap = hgap;
    myVgap = vgap;
  }
  
  

  public void addLayoutComponent(String name, Component comp) {}
  public void removeLayoutComponent(Component comp) {}
  
  public Dimension preferredLayoutSize(Container parent) {
    return new Dimension(parent.getWidth(), doLayoutContainer(parent));
  }

  public Dimension minimumLayoutSize(Container parent) {
    return new Dimension(parent.getWidth(), doLayoutContainer(parent));
  }
  
  public void layoutContainer(Container parent) {
    doLayoutContainer(parent);
  }

    
  public int doLayoutContainer(Container parent) {
    Insets theInsets = parent.getInsets();
    int theAvailableWidth = parent.getWidth();
    Component[] theComponents = parent.getComponents();
    int height = theInsets.top;
    int maxRowHeight = 0;
    int width = theInsets.left;
    for(int i=0;i<theComponents.length;i++){
      theComponents[i].setSize(theComponents[i].getPreferredSize());
      
      if(width + theComponents[i].getWidth() + theInsets.right < theAvailableWidth){
        width += theComponents[i].getWidth();
        width += myHgap;
        if(theComponents[i].getHeight() > maxRowHeight){
          maxRowHeight = theComponents[i].getHeight(); 
        }
      } else {
        height += maxRowHeight;
        height += myVgap;
        maxRowHeight = theComponents[i].getHeight();
        width = theComponents[i].getWidth();
        width += myHgap;
      }
      theComponents[i].setLocation(width - theComponents[i].getWidth(), height);
    }
    height += maxRowHeight;
    height += theInsets.bottom;
    return height;

  }

  public int getHgap() {
    return myHgap;
  }
  
  public int getVgap() {
    return myVgap;
  }

  public void setVgap(int aVGap) {
    myVgap = aVGap;
  }
  
  public void setHgap(int aHGap) {
    myHgap = aHGap;
  }

}
