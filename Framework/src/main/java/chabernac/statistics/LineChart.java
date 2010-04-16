package chabernac.statistics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class LineChart extends Component{
  private Random myRandom = null;
  private String myXLabel = "X";
  private String myYLabel = "Y";
  private double xMin, xMax, yMin, yMax, width, height, xFactor, yFactor;
  private Hashtable preferences = null;
  private int borderLeft = 30;
  private int borderRight = 10;
  private int borderTop = 10;
  private int borderBottom = 30;
  private Vector myDataVector = null;
  private Point2D.Double myOrigin = new Point2D.Double(0,0);
  private boolean originXMin = true;
  private boolean originYMin = true;
  private int xAidLines = 5;
  private int yAidLines = 5;
  private Point2D.Double mySelectedPoint = null;
  private Color background = null;
  
  
  public LineChart(){
    myDataVector = new Vector();
    myRandom = new Random();
    preferences = new Hashtable();
    addMouseListener(new MyMouseAdapter());
  }
  
  public void addData(Data aData){
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
    if(background != null){
      g.setColor(background);
      g.fillRect(0,0,getWidth(),getHeight());
    }
    
    width = getWidth() - (borderLeft + borderRight);
    height = getHeight() - (borderBottom + borderTop);
    xFactor = (xMax - xMin) / width;
    yFactor = (yMax - yMin) / height;
    
    g.setColor(Color.black);
    
    if(originXMin) myOrigin = new Point2D.Double(xMin, myOrigin.y);
    if(originYMin) myOrigin = new Point2D.Double(myOrigin.x, yMin);
    Point theOrigin = converPointToScreen(myOrigin);
    
    
    
    //draw aid lines
    g.setColor(Color.lightGray);
    double xSpace = width / (xAidLines + 1);
    double ySpace = height / (yAidLines + 1);
    
    double x = borderLeft;
    double y = getHeight() - borderBottom;
    
    for(int i=1;i<=xAidLines;i++){
      x += xSpace;
      g.drawLine((int)x, (int)y, (int)x, borderTop);
    }
    
    x = borderLeft;
    
    for(int j=1;j<=yAidLines;j++){
      y -= ySpace;
      g.drawLine((int)x, (int)y, (int)(x + width), (int)y);
    }
    
    g.setColor(Color.black);
    //  draw x ax
    int x1 =  borderLeft;
    int y1 =  theOrigin.y;
    int x2 =  (int)(width + borderLeft);
    int y2 =  theOrigin.y;
    g.drawLine( x1, y1, x2, y2);
    g.drawString(myXLabel, (x2 - (borderLeft + myXLabel.length() * 4)), (y1 - 5));
    String maxXString = Double.toString(xMax);
    g.drawString(Double.toString(xMin), x1, y1 + 15);
    g.drawString(maxXString, x2 - 10 - (maxXString.length() * 4), y1 + 15);
    
    //draw y ax
    x1 =  theOrigin.x;
    y1 =  borderTop;
    x2 =  x1;
    y2 =  (int)(height + borderTop);
    g.drawLine( x1, y1, x2, y2);
    g.drawString(myYLabel, (x1 + 5), borderTop + 10);
    String minYString = Double.toString(yMin);
    g.drawString(minYString, x1 - 10 - (minYString.length() * 4), y2);
    g.drawString(Double.toString(xMax), x1 - 10 - (minYString.length() * 4), y1 + 10);
    
    
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
      for(int j=0;j<theValues.size();j++){
        previous = current;
        Point2D.Double theCurrentPoint = (Point2D.Double)theValues.elementAt(j);
        current = converPointToScreen(theCurrentPoint);
        
        
        if(theCurrentPoint == mySelectedPoint){
          g.setColor(Color.black);
          g.fillRect( current.x - 4, current.y - 4 , 8, 8);
          g.setColor(theColor);
        }
        
        g.fillOval( current.x - 2, current.y - 2 , 4, 4);
        
        if(previous != null){
          g.drawLine( previous.x, previous.y, current.x, current.y);
        }
      }
      current = null;
    }
    
    if(mySelectedPoint != null){
      g.setColor(Color.black);
      String point = mySelectedPoint.getX() + "," + mySelectedPoint.getY(); 
      g.drawString(point, getWidth() - borderRight - (point.length() * 5), borderTop  + 10);
    }
    
    
  }
  
  private Point converPointToScreen(Point2D.Double aPoint){
    return new Point( borderLeft + (int)((aPoint.x - xMin) / xFactor), (int)(getHeight() - borderBottom)- (int)((aPoint.y - yMin) / yFactor));
  }
  
  private Point2D.Double convertPointToWorld(Point aPoint){
    return new Point2D.Double( (aPoint.x - borderLeft)  * xFactor + xMin, (getHeight() - borderBottom - aPoint.y ) *  yFactor + yMin);
  }
  
  public int getBorderBottom() {
    return borderBottom;
  }
  public void setBorderBottom(int borderBottom) {
    this.borderBottom = borderBottom;
  }
  public int getBorderLeft() {
    return borderLeft;
  }
  public void setBorderLeft(int borderLeft) {
    this.borderLeft = borderLeft;
  }
  public int getBorderRight() {
    return borderRight;
  }
  public void setBorderRight(int borderRight) {
    this.borderRight = borderRight;
  }
  public int getBorderTop() {
    return borderTop;
  }
  public void setBorderTop(int borderTop) {
    this.borderTop = borderTop;
  }
  public Point2D.Double getMyOrigin() {
    return myOrigin;
  }
  public void setMyOrigin(Point2D.Double myOrigin) {
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
  public boolean isOriginXMin() {
    return originXMin;
  }
  public void setOriginXMin(boolean originXMin) {
    this.originXMin = originXMin;
  }
  public boolean isOriginYMin() {
    return originYMin;
  }
  public void setOriginYMin(boolean originYMin) {
    this.originYMin = originYMin;
  }
  public int getXAidLines() {
    return xAidLines;
  }
  public void setXAidLines(int aidLines) {
    xAidLines = aidLines;
  }
  public int getYAidLines() {
    return yAidLines;
  }
  public void setYAidLines(int aidLines) {
    yAidLines = aidLines;
  }
  public Color getBackground() {
    return background;
  }
  public void setBackground(Color background) {
    this.background = background;
  }
  private Color getColorForChart(int aChartNr){
    switch(aChartNr){
      case 1: return new Color(0,0,200);
      case 2: return new Color(200,0,0);
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
  
  private void selectClosestPoint(Point2D.Double aPoint){
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
  
  private class MyMouseAdapter extends MouseAdapter{
    public void mousePressed(MouseEvent e) {
      Point thePoint = e.getPoint();
      selectClosestPoint(convertPointToWorld(thePoint));
      repaint();
    }
    
  }
  
  
  

}

