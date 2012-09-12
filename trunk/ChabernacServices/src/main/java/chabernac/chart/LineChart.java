/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */

package chabernac.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;


/**
 *
 * @version v1.0.0      Sep 11, 2006
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 11, 2006 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class LineChart extends Paintable{
	private final static Color TEXTCOLOR  = new Color(0,0,200);
	private final static BasicStroke STROKE_THICK = new BasicStroke(2.0f);
	private final static BasicStroke STROKE_THIN = new BasicStroke(1.0f);
	private final static Font SMALLFONT = new Font("ARIAL", Font.BOLD, 8);
	private final static Font NORMALFONT = new Font("ARIAL", Font.BOLD, 10);
//	private final static Font TITLEFONT = new Font("HELVETICA", Font.BOLD, 11);
	private final static Font COPYRIGHTFONT = new Font("HELVETICA", Font.PLAIN, 8);
	private final static Font DEFAULTFONT = new Font("DIALOG", Font.BOLD, 12);

	private Random myRandom = null;
	private String myXLabel = "X";
	private String myYLabel = "Y";
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private double width;
	private double height;
	private double xFactor;
	private double yFactor;
	private Hashtable preferences = null;
	private int borderLeft = 30;
	private int borderRight = 10;
	private int borderTop = 10;
	private int borderBottom = 30;
	private Vector myDataVector = null;
	private Point2D.Double myOrigin = new Point2D.Double(0,0);
	private Point myScreenOrigin = null;
	private boolean originXMin = true;
	private boolean originYMin = true;
	private int xAidLines = 5;
	private int yAidLines = 5;
	private Point2D.Double mySelectedPoint = null;
	private Color background = new Color(240,240,255);
	private String chartName = "";
	private boolean drawConnectors = true;
	private iValueFormatter xFormatter = null;
	private iValueFormatter yFormatter = null;

	public LineChart(){
		this(300,300);
	}

	public LineChart(int aWidth, int aHeight){
		super(aWidth, aHeight);
		myDataVector = new Vector();
		myRandom = new Random();
		preferences = new Hashtable();
	}

	public void addData(Data aData){
		aData.findBorders();
		if(myDataVector.size() == 0){
			xMin = aData.getMinX();
			xMax = aData.getMaxX();
			yMin = aData.getMinY();
			yMax = aData.getMaxY();
		} else {
			if(aData.getMinX() < xMin) xMin = aData.getMinX();
			if(aData.getMaxX() > xMax) xMax = aData.getMaxX();
			if(aData.getMinY() < yMin) yMin = aData.getMinY();
			if(aData.getMaxY() > yMax) yMax = aData.getMaxY();
		}
		myDataVector.add(aData);
		aData.sort();
		Hashtable theDataPreference = new Hashtable();
		theDataPreference.put("color", getColorForChart(myDataVector.size()));
		preferences.put(aData, theDataPreference);
	}

	public void paint(Graphics g){
		//System.out.println(g.getFont());
		Graphics2D theGraphics = (Graphics2D)g;
		RenderingHints theMap = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		theGraphics.setRenderingHints(theMap);


		if(!chartName.equals("") && borderBottom < 40) borderBottom = 40;

		if(background != null){
			g.setColor(background);
			g.fillRect(0,0,getWidth(),getHeight());
		}

		drawCopyright(g);

		width = getWidth() - (borderLeft + borderRight);
		height = getHeight() - (borderBottom + borderTop);
		xFactor = (xMax - xMin) / width;
		yFactor = (yMax - yMin) / height;

		g.setColor(Color.black);

		if(originXMin) myOrigin = new Point2D.Double(xMin, myOrigin.y);
		if(originYMin) myOrigin = new Point2D.Double(myOrigin.x, yMin);
		myScreenOrigin = converPointToScreen(myOrigin);

		drawAidLines(g);
		drawData(g);
		drawAxes(g);

		if(mySelectedPoint != null){
			g.setColor(Color.black);
			String point = mySelectedPoint.getX() + "," + mySelectedPoint.getY(); 
			g.drawString(point, getWidth() - borderRight - (point.length() * 5), borderTop  + 10);
		}

		if(!chartName.equals("")){
			g.setColor(TEXTCOLOR);
			g.setFont(DEFAULTFONT);
			g.drawString(chartName, getWidth() /  2 - chartName.length() * 2, getHeight() - 10);
		}
	}

	private void drawCopyright(Graphics g){
		SimpleDateFormat theFormat = new SimpleDateFormat("dd/MM/yyyy");
		g.setColor(Color.gray);
		g.setFont(COPYRIGHTFONT);
		g.drawString("© Guy Chauliac " + theFormat.format(new Date()), 7,getHeight());
	}

	private void drawAxes(Graphics g){
		Graphics2D theGraphics = (Graphics2D)g;
		theGraphics.setStroke(STROKE_THIN);
		g.setFont(NORMALFONT);
		g.setColor(Color.black);
		//  draw x ax
		int x1 =  borderLeft;
		int y1 =  myScreenOrigin.y;
		int x2 =  (int)(width + borderLeft);
		int y2 =  myScreenOrigin.y;
		g.drawLine( x1, y1, x2, y2);
		g.drawString(myXLabel, (x2 - (borderLeft + myXLabel.length() * 4)), (y1 - 5));
//		String maxXString = Double.toString(xMax);
		//g.drawString(Double.toString(xMin), x1, y1 + 15);
		//g.drawString(maxXString, x2 - 10 - (maxXString.length() * 4), y1 + 15);

		//draw y ax
		x1 =  myScreenOrigin.x;
		y1 =  borderTop;
		x2 =  x1;
		y2 =  (int)(height + borderTop);
		g.drawLine( x1, y1, x2, y2);
		g.drawString(myYLabel, (x1 + 5), borderTop + 10);
//		String minYString = Double.toString(yMin);
//		String maxYString = Double.toString(yMax);
		//g.drawString(minYString, x1 - 10 - (minYString.length() * 4), y2);
		//g.drawString(maxYString, x1 - 10 - (maxYString.length() * 4), y1 + 10);
	}

	private void drawAidLines(Graphics g){
		Graphics2D theGraphics = (Graphics2D)g;
		theGraphics.setStroke(STROKE_THIN);
		g.setFont(SMALLFONT);
		NumberFormat theFormat = NumberFormat.getInstance(new Locale("nl","BE"));
//		draw aid lines
		double diff = (xMax - xMin) / (xAidLines);
		double log = Math.floor(Math.log(diff) / Math.log(10));
		if(log < 0){
			theFormat.setMaximumFractionDigits(-(int)log);
			theFormat.setMinimumFractionDigits(-(int)log);
		} else {
			theFormat.setMaximumFractionDigits(0);
			theFormat.setMinimumFractionDigits(0);
		}

		double newdiff = Math.pow(10, log);
		double factor = Math.floor(diff / newdiff);
		newdiff *= factor;

		double x = Math.floor(xMin / newdiff) * newdiff;
		x += newdiff;

		while(x < xMax){
			Point p1 = converPointToScreen(new Point2D.Double(x, yMin));
			Point p2 = converPointToScreen(new Point2D.Double(x, yMax));
			g.setColor(Color.lightGray);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			String theXValue = "";
			if(xFormatter != null) theXValue = xFormatter.formatValue(x);
			else theXValue = theFormat.format(x);

			g.setColor(Color.black);
			g.drawString(theXValue, p1.x - theXValue.length() * 2, p1.y + 15);
			x += newdiff;
		}

		double diffY = (yMax - yMin) / (yAidLines);
		double logY = Math.floor(Math.log(diffY) / Math.log(10));

		if(logY < 0){
			theFormat.setMaximumFractionDigits(-(int)logY);
			theFormat.setMinimumFractionDigits(-(int)logY);
		} else {
			theFormat.setMaximumFractionDigits(0);
			theFormat.setMinimumFractionDigits(0);
		}

		double newdiffY = Math.pow(10, logY);
		double factorY = Math.floor(diffY / newdiffY);
		newdiffY *= factorY;

		double y = Math.floor(yMin / newdiffY) * newdiffY;
		y += newdiffY;

		while(y < yMax){
			Point p1 = converPointToScreen(new Point2D.Double(xMin,y));
			Point p2 = converPointToScreen(new Point2D.Double(xMax,y));
			g.setColor(Color.lightGray);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			String theYValue = "";
			if(yFormatter != null) theYValue = yFormatter.formatValue(y);
			else theYValue = theFormat.format(y);
			g.setColor(Color.black);
			g.drawString(theYValue, p1.x - 10 - theYValue.length() * 4, p1.y);
			y += newdiffY;
		}
	}

	private void drawData(Graphics g){
		Graphics2D theGraphics = (Graphics2D)g;
		theGraphics.setStroke(STROKE_THICK);

		//Draw lines
		Data theData = null;
		Vector theValues = null;
		Point previous = null;
		Point current = null;

		Hashtable thePreferences = null;
		for(int i=0;i<myDataVector.size();i++){
			theData = (Data)myDataVector.elementAt(i);
			theValues = (Vector)theData.getDataVector();
			thePreferences = (Hashtable)preferences.get(theData);
			Color theColor = (Color)thePreferences.get("color"); 
			g.setColor(theColor);
			//g.drawString(theData.getName(), getWidth() - 20 - borderRight - theData.getName().length() * 4, borderTop + (i * 15) + 20);
			g.drawString(theData.getName(), getWidth() - borderRight - 50, borderTop + (i * 15) + 20);
			for(int j=0;j<theValues.size();j++){
				previous = current;
				Point2D.Double theCurrentPoint = (Point2D.Double)theValues.elementAt(j);
				current = converPointToScreen(theCurrentPoint);


				if(theCurrentPoint == mySelectedPoint){
					g.setColor(Color.black);
					g.fillRect( current.x - 4, current.y - 4 , 8, 8);
					g.setColor(theColor);
				}

				if(drawConnectors) g.fillOval( current.x - 2, current.y - 2 , 4, 4);

				if(previous != null){
					g.drawLine( previous.x, previous.y, current.x, current.y);
				}
			}
			current = null;
		}
	}

	public void syncAllData(){
		Point2D.Double theMinPoint = (Point2D.Double)((Data)myDataVector.get(0)).getDataVector().get(0);
		Point2D.Double thePoint = null;
		for(int i=1;i<myDataVector.size();i++){
      Vector theData = ((Data)myDataVector.get(i)).getDataVector();
      if(theData.size() > 0){
  			thePoint = (Point2D.Double)((Data)myDataVector.get(i)).getDataVector().get(0); 
  			if( thePoint.getX() < theMinPoint.getX() ){
  				theMinPoint = thePoint;
  			}
      }
		}
		Data theData = null;
		for(int i=0;i<myDataVector.size();i++){
			theData = (Data)myDataVector.get(i);
			if( theData.getDataVector().size() == 0 || ((Point2D.Double)theData.getDataVector().get(0)).getX() != theMinPoint.getX()){
				theData.addValue(theMinPoint.getX(), 0);
				theData.fillZeros();
			}
		}
	}

	protected Point converPointToScreen(Point2D.Double aPoint){
		return new Point( borderLeft + (int)((aPoint.x - xMin) / xFactor), (int)(getHeight() - borderBottom)- (int)((aPoint.y - yMin) / yFactor));
	}


	protected Point2D.Double convertPointToWorld(Point aPoint){
		return new Point2D.Double( (aPoint.x - borderLeft)  * xFactor + xMin, (getHeight() - borderBottom - aPoint.y ) *  yFactor + yMin);
	}


	/**
	 * @return  the borderBottom
	 */
	public int getBorderBottom() {
		return borderBottom;
	}
	/**
	 * @param borderBottom  the borderBottom to set
	 */
	public void setBorderBottom(int borderBottom) {
		this.borderBottom = borderBottom;
	}
	/**
	 * @return  the borderLeft
	 */
	public int getBorderLeft() {
		return borderLeft;
	}
	/**
	 * @param borderLeft  the borderLeft to set
	 */
	public void setBorderLeft(int borderLeft) {
		this.borderLeft = borderLeft;
	}
	/**
	 * @return  the borderRight
	 */
	public int getBorderRight() {
		return borderRight;
	}
	/**
	 * @param borderRight  the borderRight to set
	 */
	public void setBorderRight(int borderRight) {
		this.borderRight = borderRight;
	}
	/**
	 * @return  the borderTop
	 */
	public int getBorderTop() {
		return borderTop;
	}
	/**
	 * @param borderTop  the borderTop to set
	 */
	public void setBorderTop(int borderTop) {
		this.borderTop = borderTop;
	}
	/**
	 * @return  the myOrigin
	 */
	public Point2D.Double getMyOrigin() {
		return myOrigin;
	}
	public void setOrigin(Point2D.Double myOrigin) {
		this.myOrigin = myOrigin;
	}
	public String getXLabel() {
		return myXLabel;
	}
	public void setXLabel(String myXLabel) {
		this.myXLabel = myXLabel;
	}
	public String getYLabel() {
		return myYLabel;
	}
	public void setYLabel(String myYLabel) {
		this.myYLabel = myYLabel;
	}
	/**
	 * @return  the originXMin
	 */
	public boolean isOriginXMin() {
		return originXMin;
	}
	/**
	 * @param originXMin  the originXMin to set
	 */
	public void setOriginXMin(boolean originXMin) {
		this.originXMin = originXMin;
	}
	/**
	 * @return  the originYMin
	 */
	public boolean isOriginYMin() {
		return originYMin;
	}
	/**
	 * @param originYMin  the originYMin to set
	 */
	public void setOriginYMin(boolean originYMin) {
		this.originYMin = originYMin;
	}
	/**
	 * @return  the xAidLines
	 */
	public int getXAidLines() {
		return xAidLines;
	}
	/**
	 * @param aidLines 
	 * @param xAidLines  the xAidLines to set
	 */
	public void setXAidLines(int aidLines) {
		xAidLines = aidLines;
	}
	/**
	 * @return  the yAidLines
	 */
	public int getYAidLines() {
		return yAidLines;
	}
	/**
	 * @param aidLines 
	 * @param yAidLines  the yAidLines to set
	 */
	public void setYAidLines(int aidLines) { 
		yAidLines = aidLines;
	}
	/**
	 * @return  the background
	 */
	public Color getBackground() {
		return background;
	}
	/**
	 * @param background  the background to set
	 */
	public void setBackground(Color background) {
		this.background = background;
	}
	/**
	 * @return  the xMax
	 */
	public double getXMax() {
		return xMax;
	}
	/**
	 * @param max 
	 * @param xMax  the xMax to set
	 */
	public void setXMax(double max) {
		xMax = max;
	}
	/**
	 * @return  the xMin
	 */
	public double getXMin() {
		return xMin;
	}
	/**
	 * @param min 
	 * @param xMin  the xMin to set
	 */
	public void setXMin(double min) {
		xMin = min;
	}
	/**
	 * @return  the yMax
	 */
	public double getYMax() {
		return yMax;
	}
	/**
	 * @param max 
	 * @param yMax  the yMax to set
	 */
	public void setYMax(double max) {
		yMax = max;
	}
	/**
	 * @return  the yMin
	 */
	public double getYMin() {
		return yMin;
	}
	/**
	 * @param min 
	 * @param yMin  the yMin to set
	 */
	public void setYMin(double min) {
		yMin = min;
	}
	/**
	 * @return  the chartName
	 */
	public String getChartName() {
		return chartName;
	}
	public void setTitle(String chartName) {
		this.chartName = chartName;
	}
	public Vector getData(){
		return myDataVector;
	}
	/**
	 * @return  the drawConnectors
	 */
	public boolean isDrawConnectors() {
		return drawConnectors;
	}
	/**
	 * @param drawConnectors  the drawConnectors to set
	 */
	public void setDrawConnectors(boolean drawConnectors) {
		this.drawConnectors = drawConnectors;
	}
	/**
	 * @return  the yFactor
	 */
	public double getYFactor() {
		return yFactor;
	}
	/**
	 * @param factor 
	 * @param yFactor  the yFactor to set
	 */
	public void setYFactor(double factor) {
		yFactor = factor;
	}
	/**
	 * @return  the yFormatter
	 */
	public iValueFormatter getYFormatter() {
		return yFormatter;
	}
	/**
	 * @param formatter 
	 * @param yFormatter  the yFormatter to set
	 */
	public void setYFormatter(iValueFormatter formatter) {
		yFormatter = formatter;
	}
	/**
	 * @return  the xFormatter
	 */
	public iValueFormatter getXFormatter() {
		return xFormatter;
	}
	/**
	 * @param formatter 
	 * @param xFormatter  the xFormatter to set
	 */
	public void setXFormatter(iValueFormatter formatter) {
		xFormatter = formatter;
	}
	private Color getColorForChart(int aChartNr){
		switch(aChartNr){
		case 1: return new Color(200,0,0);
		case 2: return new Color(0,0,200);
		case 3: return new Color(0,200,0);
		case 4: return new Color(0,200,200);
		case 5: return new Color(200,200,0);
		case 6: return new Color(200,0,200);
		case 7: return new Color(200,200,200);
		default: return new Color(Math.abs(myRandom.nextInt()) % 255, 
				Math.abs(myRandom.nextInt()) % 255,
				Math.abs(myRandom.nextInt()) % 255) ;
		}
	}

	protected void selectClosestPoint(Point2D.Double aPoint){
		double invClosestDistance = 0; 
		Data theData = null;
		Vector theValues = null;
		Point2D.Double thePoint = null;
		for(int i=0;i<myDataVector.size();i++){
			theData = (Data)myDataVector.elementAt(i);
			theValues = (Vector)theData.getDataVector();
			for(int j=0;j<theValues.size();j++){
				thePoint = (Point2D.Double)theValues.elementAt(j);
				double invDistance = 1 / thePoint.distance(aPoint);
				if(invDistance > invClosestDistance){
					invClosestDistance = invDistance;
					mySelectedPoint = thePoint;
				}
			}
		}
	}
}

