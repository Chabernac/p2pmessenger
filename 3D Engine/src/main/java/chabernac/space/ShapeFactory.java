package chabernac.space;

import java.awt.Color;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.PolarPoint3D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class ShapeFactory{

	public static Shape makeCube(Point3D anOrigPoint, float aWidth, float aHeight, float aDepth) throws PolygonException{
		Shape theShape = new Shape(8);

		Point3D thePoint1 = anOrigPoint;
		Point3D thePoint2 = new Point3D(anOrigPoint.x + aWidth, anOrigPoint.y, anOrigPoint.z);
		Point3D thePoint3 = new Point3D(thePoint2.x, thePoint2.y + aHeight, anOrigPoint.z);
		Point3D thePoint4 = new Point3D(anOrigPoint.x, thePoint3.y, anOrigPoint.z);
		Point3D thePoint5 = new Point3D(anOrigPoint.x, anOrigPoint.y, anOrigPoint.z + aDepth);
		Point3D thePoint6 = new Point3D(thePoint2.x, anOrigPoint.y, thePoint5.z);
		Point3D thePoint7 = new Point3D(thePoint6.x , thePoint3.y, thePoint5.z);
		Point3D thePoint8 = new Point3D(thePoint5.x , thePoint3.y, thePoint5.z);

		Polygon thePolygon = null;

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.color = new Color(200,0,0);
		thePolygon.done();
		theShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.color = new Color(0,200,0);
		thePolygon.done();
		theShape.addPolygon(thePolygon);


		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.color = new Color(0,0,200);
		thePolygon.done();
		theShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.color = new Color(200,200,0);
		thePolygon.done();
		theShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.color = new Color(0,200,200);
		thePolygon.done();
		theShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.color = new Color(200,0,200);
		thePolygon.done();
		theShape.addPolygon(thePolygon);
		theShape.done();
		return theShape;
	}

	public static Shape makeSphere(Point3D anOrigin, float aRadius, int aNrOfSections){
		System.out.println("Calculating sphere");
		Shape theShape = new Shape(aNrOfSections *  aNrOfSections);

		Point3D[][] thePointArray = new Point3D[aNrOfSections / 2 + 1][aNrOfSections+1];

		float startAngle = (float)0.001;
		float deltah = 2 * (float)Math.PI  / aNrOfSections;
    float deltav = (2 * (float)Math.PI  -  4  * startAngle ) / aNrOfSections;

		for(int i=0;i<aNrOfSections / 2  + 1;i++){
			float beta = startAngle - (float)Math.PI/2 + i *  deltav;

			for(int j=0;j<=aNrOfSections;j++){
				float alpha= j * deltah;
				thePointArray[i][j] = new Point3D(new PolarPoint3D(alpha, beta, aRadius));
			}
		}
		
		/*
		Polygon theBottomPolygon = new Polygon(aNrOfSections);
		Polygon theTopPolygon = new Polygon(aNrOfSections);
		
		for(int k=0;k<aNrOfSections;k++){
			theBottomPolygon.addVertex(new Vertex(thePointArray[0][k]));
			theTopPolygon.addVertex(new Vertex(thePointArray[thePointArray.length - 1][k]));
		}
		
		theBottomPolygon.done();
		theShape.addPolygon(theBottomPolygon);
		theTopPolygon.done();
		theShape.addPolygon(theTopPolygon);
		*/

		
		for(int i=0, j=1;j<thePointArray.length;i++,j++){
			for(int k=0, l=1;k<aNrOfSections;k++,l++){
				//l = (k + 1) % aNrOfSections;
				Polygon thePolygon = null;
				//if(i==0){
//					thePolygon = new Polygon(3);
//					thePolygon.addVertex(new Vertex(thePointArray[i][0]));
//					thePolygon.addVertex(new Vertex(thePointArray[j][l]));
//					thePolygon.addVertex(new Vertex(thePointArray[j][k]));
//				} 
//			if(j == thePointArray.length -1){
//					thePolygon = new Polygon(3);
//					thePolygon.addVertex(new Vertex(thePointArray[i][k]));
//					thePolygon.addVertex(new Vertex(thePointArray[i][l]));
//					thePolygon.addVertex(new Vertex(thePointArray[j][0]));
//				} else {
					thePolygon = new Polygon(4);
					thePolygon.addVertex(new Vertex(thePointArray[i][k]));
					thePolygon.addVertex(new Vertex(thePointArray[i][l]));
					thePolygon.addVertex(new Vertex(thePointArray[j][l]));
					thePolygon.addVertex(new Vertex(thePointArray[j][k]));
					thePolygon.done();
					theShape.addPolygon(thePolygon);
//				}
//				thePolygon.done();
//				theShape.addPolygon(thePolygon);
			}
		}
		

		theShape.done();

		System.out.println("Translating sphere");
		Transformation theTransformation = new Transformation();
		theTransformation.addTransformation(MatrixOperations.buildTranslationMatrix(new GVector(anOrigin)));

		theShape.translate(theTransformation);


		System.out.println("Sphere calculation done");
		return theShape;
	}

	public static Shape makeSinglePolygonShape(Point3D aOrigin, int aWidth, int aHeight){
		Shape theShape = new Shape(1);
		Polygon thePolygon = new Polygon(4);

		thePolygon.addVertex(new Vertex(aOrigin));
		thePolygon.addVertex(new Vertex(new Point3D(aOrigin.x + aWidth, aOrigin.y, aOrigin.z)));
		thePolygon.addVertex(new Vertex(new Point3D(aOrigin.x + aWidth, aOrigin.y - aHeight, aOrigin.z)));
		thePolygon.addVertex(new Vertex(new Point3D(aOrigin.x, aOrigin.y - aHeight, aOrigin.z)));

		thePolygon.floatSided = true;
		thePolygon.done();
		theShape.addPolygon(thePolygon);
		theShape.done();
		return theShape;
	}

	public static void main(String args[]){
		ShapeFactory.makeSphere(new Point3D(100,100,100), 5, 5);
	}
}