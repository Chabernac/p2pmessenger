/*
 * Created on 18-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;


public class ZBuffer implements iDepthBuffer{
//  private static Logger LOGGER = Logger.getLogger(ZBuffer.class);
  private float myZBuffer[];
//  private int myAlphaBuffer[];
  private float myTransparencyBuffer[];
  
  private int cycle = 0;
  private boolean isBufferEnabled = true;
  private int myWidth;
//  private int myHeight;
  private int mySize;
  


  public ZBuffer(int aWidth, int aHeight){
    myWidth = aWidth;
//    myHeight = aHeight;
    mySize = aWidth * aHeight;
    myZBuffer = new float[mySize];
//    myAlphaBuffer = new int[mySize];
    myTransparencyBuffer = new float[mySize];
    clearBuffer();
  }

  public void clearBuffer(){
    if(isBufferEnabled){ 
      cycle = cycle + 1;
      if(cycle == 0){
        for(int i=0;i<mySize;i++){
          myZBuffer[i] = 0;
          myTransparencyBuffer[i] = 0;
        }
      }
    }
  }
  
  public boolean isDrawPixel(int x, int y, float anInverseDepth){
    int i = y * myWidth + x; 

    if( i < 0 || i >= mySize || anInverseDepth < 0 || anInverseDepth > 1){
      return false;
    }
   
    float cycleDepth = cycle + anInverseDepth;
    
    if(cycleDepth > myZBuffer[i]){
      //this pixel will be drawn only at this point we should calculate the color and apply pixel shading
      myZBuffer[i] = cycleDepth;
      return true;
    }
    
    return false;

  }
  

//  public void setValueAt(int x, int y, float aDepth, int aColor, boolean ignoreDepth){
//    int i = y * myWidth + x; 
//
//    if( i < 0 || i >= mySize || aDepth < 0 || aDepth > 1){
//      return;
//    }
//
//    int theTransparency = (aColor >> 24) & 0xff;
//
//    if( theTransparency == 0x00 ){ 
//      return;  
//    }
//
//    //true when 0x00 < transparency < 0xff 
//    boolean transparent = (theTransparency != 0xff);
//
//    float cycleDepth = cycle + aDepth;
//
//    if(transparent && cycleDepth > myZBuffer[i]){
//      myAlphaBuffer[i] = aColor;
//      myTransparencyBuffer[i] = cycleDepth;
//
//      setPixelAt(x,y, mixColors(aColor, getPixelAt(x,y)));
//    } else {
//      if(cycleDepth > myZBuffer[i]){
//        //this pixel will be drawn only at this point we should calculate the color and apply pixel shading
//        myZBuffer[i] = cycleDepth;
//        if(cycleDepth < myTransparencyBuffer[i]){
//          setPixelAt(x,y, mixColors(myAlphaBuffer[i], aColor));
//        } else {
//          setPixelAt(x,y, aColor);
//        }
//      } 
//    }
//  }
//
//  private int mixColors(int aTransparencyColor, int aColor){
//    int alpha = aTransparencyColor >> 24 & 0xff;
//        int red = aTransparencyColor >> 16 & 0xff;
//        int green = aTransparencyColor >> 8 & 0xff;
//        int blue = aTransparencyColor & 0xff;
//
//        //int alpha2 = aColor >> 24 & 0xff;
//        int red2 = aColor >> 16 & 0xff;
//        int green2 = aColor >> 8 & 0xff;
//        int blue2 = aColor & 0xff;
//
//        float thePercentage = alpha / 256D;
//        float theInvPercentage = 1 - thePercentage;
//
//        red = (int)(thePercentage * red + theInvPercentage * red2); 
//        green = (int)(thePercentage * green + theInvPercentage * green2);
//        red = (int)(thePercentage * red + theInvPercentage * blue2);
//
//        return (0xff << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | (blue << 0 & 0x000000FF);
//  }


  //protected void prepareImage() {}

  public boolean isBufferEnabled() {
    return isBufferEnabled;
  }

  public void setBufferEnabled(boolean isBufferEnabled) {
    this.isBufferEnabled = isBufferEnabled;
  }
}
