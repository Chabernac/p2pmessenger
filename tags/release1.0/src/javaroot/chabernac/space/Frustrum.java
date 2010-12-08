package chabernac.space;

import chabernac.space.geom.Plane;

public class Frustrum{
  private int myPlanesSize;
  private int myCurrentPlane = 0;
  public Plane[] myPlanes;
  
  public Frustrum(int planes){
    myPlanesSize = planes;
    initialize();
    clear();
  }
  
  private void initialize(){
    myPlanes = new Plane[myPlanesSize];
  }
  
  public void addPlane(Plane aPlane){
    myPlanes[myCurrentPlane++] = aPlane;
  }
  
  public void clear(){
    for(int i=0;i<myPlanesSize;i++){
      myPlanes[i] = null;
    }
    myCurrentPlane = 0;
  }
}