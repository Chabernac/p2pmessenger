package chabernac.space.geom;

public class PolarPoint3D {
  public float alpha;
  public float beta;
  public float radius;
  
  public PolarPoint3D(Point3D aPoint){
    radius = new GVector(aPoint).length();
    beta = (float)Math.asin(aPoint.y / radius);
    float theValue = aPoint.x / (radius * (float)Math.cos(beta));
    if(theValue > 1){
      theValue = 1;
    }
    if(theValue < -1){
      theValue = -1;
    }
    alpha = (float)Math.acos(theValue);
    if(aPoint.z < 0){
      alpha = 2 * (float)Math.PI - alpha;
    }
  }
  
  public PolarPoint3D(float alpha, float beta, float radius){
    this.alpha = alpha;
    this.beta = beta;
    this.radius = radius;
  }
  
  public float getAlpha() {
    return alpha;
  }
  public void setAlpha(float anAlpha) {
    alpha = anAlpha;
  }
  public float getBeta() {
    return beta;
  }
  public void setBeta(float anBeta) {
    beta = anBeta;
  }
  public float getRadius() {
    return radius;
  }
  public void setRadius(float anRadius) {
    radius = anRadius;
  }
  
  public String toString(){
    return "<PolarPoint alpha='" + alpha + "' beta='" + beta + "' radius='" + radius +"'>";
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof PolarPoint3D)) return false;
    PolarPoint3D thePoint = (PolarPoint3D)anObject;
    if(Math.abs(thePoint.alpha - alpha) > 0.01) return false;
    if(Math.abs(thePoint.beta - beta) > 0.01) return false;
    if(Math.abs(thePoint.radius - radius) > 0.01) return false;
    return true;
  }
  
  public static void main(String args[]){
    for(float alpha=0;alpha<2 * Math.PI;alpha += 0.01){
      for(float beta=-(float)Math.PI/2;beta<Math.PI/2;beta+= 0.01){
        PolarPoint3D thePPoint = new PolarPoint3D(alpha, beta, 1);
        Point3D theP = new Point3D(thePPoint);
        PolarPoint3D thePPoint2 = new PolarPoint3D(theP);
        if(!thePPoint.equals(thePPoint2)){
          System.out.println(thePPoint + " != " + thePPoint2);
        }
      }
    }
    
    /*
    PolarPoint3D thePPoint = new PolarPoint3D(3 * Math.PI / 2 , -Math.PI / 4, 2);
    System.out.println("Polar point: " + thePPoint);
    Point3D thePoint = new Point3D(thePPoint);
    System.out.println("Point: " + thePoint);
    thePPoint = new PolarPoint3D(thePoint);
    System.out.println("Polar point: " + thePPoint);
    System.out.println("Point: " + new Point3D(thePPoint));
    */
  }
  
  

}
