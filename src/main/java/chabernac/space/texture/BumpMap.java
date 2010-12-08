package chabernac.space.texture;

import java.io.IOException;

import chabernac.space.geom.GVector;

public class BumpMap {
  private TextureImage myImage = null;
  private GVector[] myVectorMap = null;
  private double myMaxDepth;
  private int myXOffset = 0;
  private int myYOffset = 0;
  
  public BumpMap(TextureImage anImage, double aMaxDepth){
    myImage = anImage;
    myMaxDepth = aMaxDepth;
    myVectorMap = new GVector[myImage.width * myImage.height];
    fillBumpMap();
  }
  
  private void fillBumpMap(){
    int i=0;
    for(int y=0;y<myImage.height;y++){
      for(int x=0;x<myImage.width;x++){
        GVector theXVector = new GVector(1,0, getDepth(myImage.getColorAt(x+1, y)) - getDepth(myImage.getColorAt(x-1, y))); 
        GVector theYVector = new GVector(0,1, getDepth(myImage.getColorAt(x, y + 1)) - getDepth(myImage.getColorAt(x, y - 1)));
        myVectorMap[i++] = theXVector.produkt(theYVector);
      }
    }
  }

  private double getDepth(int aColor) {
    int red   = aColor >> 16 & 0xff;
    int green = aColor >>  8 & 0xff;
    int blue  = aColor & 0xff;
    
    double theAverageColor = ((red + green + blue) / 3) - 128;
    double thePercentage = theAverageColor / 128;
    
    return thePercentage * myMaxDepth;
  }
  
  public GVector getNormalAt(int x, int y){
    x += myXOffset;
    y += myYOffset;
    for(; x < 0; x += myImage.width);
    for(; x >= myImage.width; x -= myImage.width);
    for(; y < 0; y += myImage.height);
    for(; y >= myImage.height; y -= myImage.height);
    return getNormalAt(y * myImage.width + x);
  }
  
  public GVector getNormalAt(int i){
    return myVectorMap[i];
  }
  
  public static void main(String[] args){
    try {
      TextureImage theImage = TextureFactory.getTexture("marsbump1k", false);
      BumpMap theMap = new BumpMap(theImage, 100);
      for(int i=0;i<theMap.myVectorMap.length;i++){
        System.out.println(theMap.getNormalAt(i));
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public TextureImage getImage(){
    return myImage;
  }

}
