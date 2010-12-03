package chabernac.space;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chabernac.math.MatrixException;
import chabernac.space.geom.PointShape;
import chabernac.space.geom.Shape;
import chabernac.utils.sort.FastArrayQSortAlgorithm;

public class World{
  public int mySize;
  public Shape[] myShapes;
  public int myPointShapeSize;
  public PointShape[] myPointShapes;
  private int myCurrentShape = 0;
  private int myCurrentPointShape = 0;
  private FastArrayQSortAlgorithm theSortAlgorithm = null;
  private ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
  private TranslateManagerContainer myTranslateManagerContainer = new TranslateManagerContainer();

  private ExecutorService myService = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );


  public World(int aSize){
    this(aSize, 0);
  }

  public World(int aSize, int aPointShapeSize){
    mySize = aSize;
    myPointShapeSize = aPointShapeSize;
    initialize();
  }

  private void initialize(){
    myShapes = new Shape[mySize];
    myPointShapes = new PointShape[myPointShapeSize];
    theSortAlgorithm = new FastArrayQSortAlgorithm();
    clear();
  }

  public void clear(){
    for(int i=0;i<mySize;i++){
      myShapes[i] = null;
    }
    for(int i=0;i<myPointShapeSize;i++){
      myPointShapes[i] = null;
    }
    myCurrentShape = 0;
    myCurrentPointShape = 0;
  }

  public void addShape(Shape aShape){
    myShapes[myCurrentShape++] = aShape;
  }

  public void addPointShape(PointShape aPointShape){
    myPointShapes[myCurrentPointShape++] = aPointShape;
  }

  public void done() throws PolygonException{
    optimize();
    //affectLightning();
    calculateCenterPoints();
    calculateNormalVectors();
  }

  /*
	private void affectLightning(){
		LightSource theCurrentLight = null;
		Shape theCurrentShape = null;
		Polygon theCurrentPolygon = null;
		for(int i=0;i<lightSources.size();i++){
			theCurrentLight = (LightSource)lightSources.get(i);
			for(int j=0;j<myShapes.length;j++){
				theCurrentShape = myShapes[j];
				for(int k=0;k<theCurrentShape.myPolygons.length;k++){
					theCurrentPolygon = theCurrentShape.myPolygons[k];
					theCurrentLight.applyToPolygon(theCurrentPolygon);
				}
			}
		}
	}
   */

  public void optimize(){
    if(myCurrentShape < mySize){
      Shape[] theTempShapes = new Shape[myCurrentShape];
      System.arraycopy(myShapes, 0, theTempShapes, 0, myCurrentShape);
      myShapes = theTempShapes;
      mySize = myCurrentShape;
    }
    if(myCurrentPointShape < myPointShapeSize){
      PointShape[] theTempShapes = new PointShape[myCurrentPointShape];
      System.arraycopy(myPointShapes, 0, theTempShapes, 0, myCurrentPointShape);
      myPointShapes = theTempShapes;
      myPointShapeSize = myCurrentShape;
    }
  }

  public void calculateCenterPoints(){
    for(int i=0;i<mySize;i++){
      myShapes[i].calculateCenterPoint();
    }
    for(int i=0;i<myPointShapeSize;i++){
      myPointShapes[i].calculateCenterPoint();
    }
  }

  public void calculateNormalVectors() throws PolygonException{
    for(int i=0;i<mySize;i++){
      myShapes[i].calculateNormalVectors();
    }
  }

  public void world2Cam(final Camera aCamera) throws PolygonException, MatrixException{
    //TODO optimized code for multi core processors, but does dis has the wanted effect?


    for(int i=0;i<mySize;i++){
      myShapes[i].world2Cam(aCamera);
    }
    for(int i=0;i<myPointShapeSize;i++){
      myPointShapes[i].world2Cam(aCamera);
    }

    for(LightSource theLighteSource : lightSources){
      theLighteSource.world2Cam(aCamera);
    }

  }

  //  public void world2Cam(final Camera aCamera) throws PolygonException, MatrixException{
  //    //TODO optimized code for multi core processors, but does dis has the wanted effect?
  //    
  //    final CountDownLatch theLatch = new CountDownLatch( mySize  + myPointShapeSize + lightSources.size());
  //
  //    for(int i=0;i<mySize;i++){
  //      final int theIndex = i;
  //      myService.execute( new Runnable(){
  //        public void run(){
  //          myShapes[theIndex].world2Cam(aCamera);
  //          theLatch.countDown();
  //        }
  //      });
  //    }
  //    for(int i=0;i<myPointShapeSize;i++){
  //      final int theIndex = i;
  //      myService.execute( new Runnable(){
  //        public void run(){
  //          myPointShapes[theIndex].world2Cam(aCamera);
  //          theLatch.countDown();
  //        }
  //      });
  //    }
  //
  //    for(int i=0;i<lightSources.size();i++){
  //      final int theIndex = i;
  //      myService.execute( new Runnable(){
  //        public void run(){
  //          ((LightSource)lightSources.get(theIndex)).world2Cam(aCamera);
  //          theLatch.countDown();
  //        }
  //      });
  //    }
  //    
  //    try {
  //      theLatch.await();
  //    } catch ( InterruptedException e ) {
  //    }
  //  }

  public void clip2Frustrum(Frustrum aFrustrum) throws PolygonException{
    for(int i=0;i<mySize;i++){
      myShapes[i].clip2Frustrum(aFrustrum);
    }
    for(int i=0;i<myPointShapeSize;i++){
      myPointShapes[i].clip2Frustrum(aFrustrum);
    }
  }

  public void sort() throws Exception{
    theSortAlgorithm.sort(myShapes);
    theSortAlgorithm.sort(myPointShapes);
  }

  public void addLightSource(LightSource aLightSource){
    lightSources.add(aLightSource);
  }

  public void removeLightSource(LightSource aLightSource){
    lightSources.remove(aLightSource);
  }

  public ArrayList<LightSource> getLightSources(){
    return lightSources;
  }

  public TranslateManagerContainer getTranslateManagerContainer(){
    return myTranslateManagerContainer;
  }
}
