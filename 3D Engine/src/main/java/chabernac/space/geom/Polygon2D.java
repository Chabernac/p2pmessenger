package chabernac.space.geom;

import org.apache.log4j.Logger;

import chabernac.space.texture.Texture2;

public class Polygon2D {
  private static Logger LOGGER = Logger.getLogger(Polygon2D.class);
  private static final float AFFINEBORDER = (float)Math.tan(Math.PI / 10);
  private static boolean FORCEAFFINE = true;
  private static boolean FORCENOTAFFINE = false;

  //  private Color myColor = null;
  private Vertex2D[] myVertexes = null;
  private int currentVertex = 0;
  private Line2D[] myLines = null;
  private Texture2 myTexture = null;

  public Polygon2D(int aSize){
    myVertexes = new Vertex2D[aSize];
    myLines = new Line2D[aSize];
  }

  public void addVertex(Vertex2D aVertex){
    myVertexes[currentVertex++] = aVertex;
  }

  public Vertex2D[] getVertexes(){
    return myVertexes;
  }

  public void done(){
    int j = 0;
    for(int i=0;i<myVertexes.length;i++){
      j = (i + 1 ) % myVertexes.length;
      myLines[i] = new Line2D(myVertexes[i].getPoint(), myVertexes[j].getPoint());
    }
  }

  public Vertex2D[] intersectHorizontalLine(int y){
    Vertex2D[] theVertexes = new Vertex2D[2];
    
    float time;
    int j;
    int current = 0;
    for(int i=0;i<myLines.length && current < 2;i++){
      time = myLines[i].intersectHorizontalLine(y);
      if(time >= 0 && time <= 1){
        j = ( i + 1 ) % myLines.length;
        //our horizontal goes from vertex[i] to vertex[j]
        
        Point2D thePoint = myLines[i].getPoint(time);
        
        float Sz = myVertexes[i].getInverseDepth() + time * (myVertexes[j].getInverseDepth() - myVertexes[i].getInverseDepth());
        float Su = myVertexes[i].getPerspectiveCorrectTexturePoint().x + time * (myVertexes[j].getPerspectiveCorrectTexturePoint().x - myVertexes[i].getPerspectiveCorrectTexturePoint().x);
        float Sv = myVertexes[i].getPerspectiveCorrectTexturePoint().y + time * (myVertexes[j].getPerspectiveCorrectTexturePoint().y - myVertexes[i].getPerspectiveCorrectTexturePoint().y);
        float li = myVertexes[i].getLightning() + time * (myVertexes[j].getLightning() - myVertexes[i].getLightning());
        
        float u = Su / Sz;
        float v = Sv / Sz;
        
        theVertexes[current++] = new Vertex2D( thePoint, new Point2D(u,v), 1 / Sz, li);
      }
    }
    
    //rearrange the vertexes from left to right
    if(current == 2) {
      if(theVertexes[0].getPoint().x > theVertexes[1].getPoint().x){
        Vertex2D theTempVertex = theVertexes[0];
        theVertexes[0] = theVertexes[1];
        theVertexes[1] = theTempVertex;
      }
    }
    
    return theVertexes;
  }

//  public Vertex2D[] intersectHorizontalLine(int y){
//    Vertex2D theTempVertex = null;
//    Vertex2D[] theVertexes = new Vertex2D[2];
//    float time, inversezDiff, zDiff, lDiff, uDiff, vDiff, inverseZ = 0, z, uRico = 0, vRico = 0, u = 0, v = 0;
//    int j = 0, current = 0;
//    for(int i=0;i<myLines.length;i++){
//      time = myLines[i].intersectHorizontalLine(y);
//      if(time >= 0 && time <= 1){
//        //when we found that our horizontal lines intersects with line[i] it goes from vertex[i] to vertext[j]
//        j = ( i + 1 ) % myLines.length;
//        zDiff = myVertexes[j].getDepth() - myVertexes[i].getDepth();
//        //        inversezDiff = myVertexes[j].getInverseDepth() - myVertexes[i].getInverseDepth();
//
//        lDiff = myVertexes[j].getLightning() - myVertexes[i].getLightning();
//
//        boolean isTexture = myVertexes[i].getTexturePoint() != null; 
//
//        Vector2D theDistance = Texture2.distance(myTexture, myVertexes[i].getTexturePoint(), myVertexes[j].getTexturePoint());
//        uDiff = theDistance.x;
//        vDiff = theDistance.y;
//        Vector2D.freeInstance(theDistance);
//
//        //LOGGER.debug("udiff: " + uDiff + " vdiff: " + vDiff);
//
//        //        float tanangle = Math.abs(zDiff) / myLines[i].length();
//
//        //        if(FORCENOTAFFINE || (!FORCEAFFINE && tanangle > AFFINEBORDER)){
//        z = myVertexes[i].getDepth() + zDiff * time;
//        if(zDiff != 0){
//          //          //System.out.println("not affine");
//          //          inverseZ = myVertexes[i].getInverseDepth() + inversezDiff * time;
//          //          z = 1 / inverseZ;
//          //
//          if(isTexture){
//            uRico = uDiff / zDiff;
//            vRico = vDiff / zDiff;
//            u = myVertexes[i].getTexturePoint().x + uRico * (z - myVertexes[i].getDepth()); 
//            v = myVertexes[i].getTexturePoint().y + vRico * (z - myVertexes[j].getDepth());
//          }
//          //        } else {
//          //System.out.println("affine");
//          //zdiff too small for accurate interpolation 
//          //          z = myVertexes[i].getDepth() + zDiff * time;
//          if(isTexture){
//            u = myVertexes[i].getTexturePoint().x + uDiff * time;
//            v = myVertexes[i].getTexturePoint().y + vDiff * time;
//          }
//        }
//
//        /*
//        if(u<0 || v <0){
//          LOGGER.debug("Impossible situtation, affine: "  +  !(zDiff > 1));
//          LOGGER.debug("start point: " + myVertexes[i].getPoint() + " texture point: " + myVertexes[i].getTexturePoint());
//          LOGGER.debug("end point: " + myVertexes[j].getPoint() + " texture point: " + myVertexes[j].getTexturePoint());
//          LOGGER.debug("z start: " + myVertexes[i ].getDepth() + " z end: " + myVertexes[j].getDepth());
//          LOGGER.debug("time : " + time);
//          LOGGER.debug("udiff: " + uDiff + " vdiff: " + vDiff);
//          LOGGER.debug("u: " + u + " v: " + v);
//          LOGGER.debug("Inverse z: " + inverseZ);
//          LOGGER.debug("z: " + z);
//          LOGGER.debug("urico: " + uRico);
//          LOGGER.debug("vrico: " + vRico);
//        }
//         */
//
//        //LOGGER.debug("u: " + u + " v: " + v);
//
//
//        // theVertexes[current++] = new Vertex2D(myLines[i].getPoint(time), new Point2D(u, v), z, myVertexes[i].getLightning() + lDiff * time);
//        theVertexes[current++] = Vertex2D.getInstance(myLines[i].getPoint(time), Point2D.getInstance(u, v), z, myVertexes[i].getLightning() + lDiff * time);
//        if(current == 2) {
//          if(theVertexes[0].getPoint().x > theVertexes[1].getPoint().x){
//            theTempVertex = theVertexes[0];
//            theVertexes[0] = theVertexes[1];
//            theVertexes[1] = theTempVertex;
//          }
//          return theVertexes;
//        }
//      }
//    }
//    return theVertexes;
//  }

  //  public void setColor(Color aColor){
  //    myColor = aColor;
  //  }
  //
  //  public Color getColor(){
  //    return myColor;
  //  }

  public void setTexture(Texture2 aTexture){
    myTexture = aTexture;
  }

  public Texture2 getTexture(){
    return myTexture;
  }

}
