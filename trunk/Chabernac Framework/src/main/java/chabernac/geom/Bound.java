package chabernac.geom;

public class Bound{
	private int x;
	private int y;
	private int width;
	private int height;

	public Bound(Bound aBound){
		setBound(aBound);
	}

	public Bound(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public String toString(){
		return "X: " + x + " Y: " + y + " Width: " + width + " Height: " + height;
	}

	public boolean equals(Bound aBound){
		if(x == aBound.getX() && y == aBound.getY() && width == aBound.getWidth() && height == aBound.getHeight()){
			return true;
		} else {
			return false;
		}
	}

	public void setBound(Bound aBound){
		x = aBound.getX();
		y = aBound.getY();
		width = aBound.getWidth();
		height = aBound.getHeight();
	}

	public Bound minus(Bound aBound){
		int theX = 0;
		int theY = 0;
		int theWidth = 0;
		int theHeight = 0;

		if(x == aBound.getX()){
			theX = x;
			theWidth = width;
			if(y > aBound.getY() && y < aBound.getY() + aBound.getHeight()){
				theY = aBound.getY() + aBound.getHeight();
				theHeight = y + height - theY;
			} else if(aBound.getY() > y && aBound.getY() < y + height){
				theY = y;
				theHeight = aBound.getY() - y;
			} else {
				theY = y;
				theHeight = height;
			}

		} else if(y == aBound.getY()){
			theY = y;
			theHeight= height;
			if(x > aBound.getX() && x < aBound.getX() + aBound.getWidth()){
				theX = aBound.getX() + aBound.getWidth();
				theWidth = x + width - theX;
			} else if(aBound.getX() > x && aBound.getX() < x + width){
				theX = x;
				theWidth = aBound.getX() - x;
			} else {
				theX = x;
				theWidth = width;
			}
		} else {
			return null;
		}
		return new Bound(theX,theY,theWidth,theHeight);
	}
}
