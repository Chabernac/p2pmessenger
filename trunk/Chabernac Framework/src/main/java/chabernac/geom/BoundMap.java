package chabernac.geom;

public class BoundMap{
	private int width;
	private int height;
	private int rows;
	private int columns;
	private double myRowHeight;
	private double myColumnWidth;

	public BoundMap(int width, int height, int rows, int columns){
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.columns = columns;
		myRowHeight = height / rows;
		myColumnWidth = width / columns;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public int getRowHeight(){
		return (int)myRowHeight;
	}

	public int getColumnWidth(){
		return (int)myColumnWidth;
	}

	public int getRows(){
		return rows;
	}

	public int getColumns(){
		return columns;
	}

	public Location getLocation(int x, int y){
		int xLoc;
		int yLoc;
		if(x >= width){
			xLoc = columns - 1;
		} else if (x < 0){
			xLoc = 0;
		} else {
			xLoc = (int)Math.floor(x / myColumnWidth);
		}

		if(y >= height){
			yLoc = rows - 1;
		} else if(y < 0){
			yLoc = 0;
		} else {
			yLoc = (int)Math.floor(y / myRowHeight);
		}
		return new Location(xLoc, yLoc);
	}

	public Bound getBound(Location aLocation){
		return new Bound((int)Math.round(aLocation.getX() * myColumnWidth),(int)Math.round(aLocation.getY() * myRowHeight), (int)myColumnWidth, (int)myRowHeight);
	}

	public String toString(){
		return "Width: " + width + " Height: " + height  + " Rows: " + rows + " Columns: " + columns;
	}

	public int getNr(Location aLocation){
		return aLocation.getY() * columns + aLocation.getX();
	}
}