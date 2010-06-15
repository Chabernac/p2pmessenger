/*
 * Created on 24-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.paint;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Selector implements iPaintable {
	private Rectangle myRecangle = null;
	private int myLength = 3;
	private boolean half = false;
	
	public Selector(Rectangle aRectangle){
		this(aRectangle, 3);
	}
	
	public Selector(Rectangle aRectangle, int aLength){
		myRecangle = aRectangle;
		myLength = aLength;
	}
	
	public void paint(Graphics g) {
		int x2 = myRecangle.x + myRecangle.width;
		int y2 = myRecangle.y + myRecangle.height;
		
		//left up
		g.drawLine(myRecangle.x, myRecangle.y, myRecangle.x, myRecangle.y + myLength);
		g.drawLine(myRecangle.x, myRecangle.y, myRecangle.x + myLength, myRecangle.y);
		
		//right down
		g.drawLine(x2, y2, x2 - myLength, y2);
		g.drawLine(x2, y2, x2, y2 - myLength);
		
		
		if(!half){
			//right up
			g.drawLine(x2, myRecangle.y, x2 - myLength, myRecangle.y);
			g.drawLine(x2, myRecangle.y, x2, myRecangle.y + myLength);
			
			//left down
			g.drawLine(myRecangle.x, y2, myRecangle.x, y2 - myLength);
			g.drawLine(myRecangle.x, y2, myRecangle.x + myLength, y2);
		}
	}
	
	public boolean isHalf() {
		return half;
	}
	
	public void setHalf(boolean half) {
		this.half = half;
	}
	
	
	
}
