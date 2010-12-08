package chabernac.space;

import chabernac.space.geom.Point3D;
import chabernac.space.geom.PointShape;

public class SpherePointShape extends PointShape{
  protected float myRadius;
  protected Point3D myCenterPoint;
  private int myCurrentPoint;
  private int myPoints;
  
  public SpherePointShape(Point3D aCenterPoint, float aRadius, int points){
  	super(((points / 2) + 1) * (points + 1));
	myPoints = points;
    myCenterPoint = aCenterPoint;
    myRadius = aRadius;
    generatePoints();
  }
  
  private void generatePoints(){
    float step = (float)Math.PI * 2 / myPoints;
    myCurrentPoint = 0;
    for(int alpha=-myPoints/4; alpha<= myPoints/4 ; alpha++){
      for(int beta=0;beta<=myPoints; beta++){
        generatePoint(alpha * step, beta * step, myRadius);    
      }  
    }
    System.out.println("Current point: " + myCurrentPoint);
  }
  
  private void generatePoint(float alpha, float beta, float aRadius){
    float z = aRadius * (float)Math.sin(alpha);
    float a = (float)Math.cos(alpha);
    float y = aRadius * a * (float)Math.sin(beta);
    float x = aRadius * a * (float)Math.cos(beta);
    w[myCurrentPoint] = new Point3D(myCenterPoint.x + x,myCenterPoint.y + y,myCenterPoint.z + z);
    myCurrentPoint++;
  }

  
  public float getRadius() {
    return myRadius;
  }


  public void setRadius(float aF) {
    myRadius = aF;
  }
  
  public String toString(){
    StringBuffer theBuffer = new StringBuffer();
    theBuffer.append("<PointShpereShape points=");
    for(int i=0;i<w.length;i++){
      theBuffer.append(w[i].toString() + "\n");
    }
    theBuffer.append(">");
    return theBuffer.toString();
  }

}
