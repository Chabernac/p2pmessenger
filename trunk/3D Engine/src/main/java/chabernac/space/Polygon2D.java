package chabernac.space;

import java.awt.Color;

import chabernac.space.geom.Line2D;
import chabernac.space.geom.Point2D;
import chabernac.space.texture.Texture2;

public class Polygon2D {
	private Color myColor = null;
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
		Vertex2D theTempVertex = null;
		Vertex2D[] theVertexes = new Vertex2D[2];
		double time, inversezDiff, zDiff, lDiff, uDiff, vDiff, inverseZ, z, uRico, vRico, u, v;
		int j = 0, current = 0;
		for(int i=0;i<myLines.length;i++){
			time = myLines[i].intersectHorizontalLine(y);
			if(time >= 0 && time <= 1){
				//when we found that our horizontal lines intersects with line[i] it goes from vertex[i] to vertext[j]
				j = ( i + 1 ) % myLines.length;
				zDiff = myVertexes[j].getDepth() - myVertexes[i].getDepth();
				inversezDiff = myVertexes[j].getInverseDepth() - myVertexes[i].getInverseDepth();
				
				lDiff = myVertexes[j].getLightning() - myVertexes[i].getLightning();
				uDiff = myVertexes[j].getTexturePoint().x - myVertexes[i].getTexturePoint().x;
				vDiff = myVertexes[j].getTexturePoint().y - myVertexes[i].getTexturePoint().y;
				uRico = uDiff / zDiff;
				vRico = vDiff / zDiff;
				
				inverseZ = myVertexes[i].getInverseDepth() + inversezDiff * time;
				z = 1 / inverseZ;
				
				u = myVertexes[i].getTexturePoint().x + uRico * z; 
				v = myVertexes[i].getTexturePoint().y + vRico * z;
					

				theVertexes[current++] = new Vertex2D(myLines[i].getPoint(time), new Point2D(u, v), z, myVertexes[i].getLightning() + lDiff * time);
				if(current == 2) {
					if(theVertexes[0].getPoint().x > theVertexes[1].getPoint().x){
						theTempVertex = theVertexes[0];
						theVertexes[0] = theVertexes[1];
						theVertexes[1] = theTempVertex;
					}
					return theVertexes;
				}
			}
		}
		return theVertexes;
	}
	
	public void setColor(Color aColor){
		myColor = aColor;
	}
	
	public Color getColor(){
		return myColor;
	}
	
	public void setTexture(Texture2 aTexture){
		myTexture = aTexture;
	}
	
	public Texture2 getTexture(){
		return myTexture;
	}

}
