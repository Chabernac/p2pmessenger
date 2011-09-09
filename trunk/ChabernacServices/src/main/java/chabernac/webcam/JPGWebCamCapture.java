package chabernac.webcam;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.JFrame;

import com.smaxe.os.jna.win32.support.IVideoFrameProcessor;
import com.smaxe.os.jna.win32.support.VideoCaptureDevice;
import com.smaxe.os.jna.win32.support.VideoCaptureLibrary;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.W32API.HWND;

public class JPGWebCamCapture {
  private final static ColorModel DEFAULT_COLOR_MODEL =
    new ComponentColorModel(
    ColorSpace.getInstance(ColorSpace.CS_sRGB),
    new int[] { 8, 8, 8 },
    false, false,
    Transparency.OPAQUE,
    DataBuffer.TYPE_BYTE);
  
  private final static Point ZERO_POINT = new Point(0, 0);
  
  private VideoCaptureDevice myCaptureDevice;
  private Pointer myFramePointer;
  private Dimension myDimension;
  private float myQuality;
  private boolean isFlip = true;
  
  private SampleModel mySampleModel = null;
  
  private final static int BAND_OFFSETS[] = new int[] {
    2 /* offset to Blue */,
    1 /* offset to Green */,
    0 /* offset to Red */
    }; // BAND_OFFSETS.length represents # of bands


  public JPGWebCamCapture(int aWidth, int aHeight, float aQuality){
    init();
    setDimensions(new Dimension(aWidth, aHeight));
    myQuality = aQuality;
  }
  
  private void createSampleModel(){
    mySampleModel = new PixelInterleavedSampleModel(
        DataBuffer.TYPE_BYTE /* data type for storing samples */,
        myDimension.width /* width in pixels */,
        myDimension.height /* height in pixels */,
        3 /* pixel stride */,
        myDimension.width * 3 /* line stride */,
        BAND_OFFSETS /* the offsets of all bands */);
  }

  private void init(){
    List<VideoCaptureDevice> theDevices = VideoCaptureLibrary.findAllVideoCaptureDevices();
    if(theDevices.size() > 0){
      myCaptureDevice = theDevices.get(0);
      myCaptureDevice.setFrameFlip(false);
    }

    JFrame theTestFrame = new JFrame();
    theTestFrame.setUndecorated( true );
    theTestFrame.setSize( 0, 0 );
    theTestFrame.setVisible( true );

    myFramePointer = Native.getComponentPointer(theTestFrame);

    theTestFrame.setVisible(false);
  }
  
  private void setDimensions(Dimension aDimension){
    if(myDimension == null || !myDimension.equals(aDimension)){
      myDimension = aDimension;
      createSampleModel();
    }
  }

  private byte[] createJPG(byte[] aBytes, int aWidth, int aHeight) throws IOException{
    setDimensions(new Dimension(aWidth, aHeight));
    BufferedImage theBufferedImage = new BufferedImage(DEFAULT_COLOR_MODEL,
        Raster.createWritableRaster(mySampleModel, new DataBufferByte(aBytes,
            myDimension.width * myDimension.height * 3, 0), ZERO_POINT),
            false, null);
    
    if(isFlip){
      theBufferedImage = flip( theBufferedImage );
    }
    
    IIOImage outputImage = new IIOImage(theBufferedImage, null, null);

    ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();
    MemoryCacheImageOutputStream theOutput = new MemoryCacheImageOutputStream( theByteArrayOutputStream );
    
    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();        
    writer.setOutput(theOutput);
    ImageWriteParam writeParam = writer.getDefaultWriteParam();
    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    writeParam.setCompressionQuality(myQuality); // float between 0 and 1, 1 for max quality.
    writer.write( null, outputImage, writeParam);
    return theByteArrayOutputStream.toByteArray();
  }

  public synchronized byte[] capture() throws WCException{
    if(myCaptureDevice == null) throw new WCException("No capture device");
    
    final ArrayBlockingQueue<byte[]> theJPGQueue = new ArrayBlockingQueue<byte[]>(1);

    myCaptureDevice.startVideoCapture(
        new HWND(myFramePointer), 
        myDimension.width, 
        myDimension.height, 
        new VideoFrameProcessor( theJPGQueue ));

    byte[] theBytes;
    try {
      theBytes = theJPGQueue.take();
    } catch ( InterruptedException e ) {
      throw new WCException("Could not wait for image bytes", e);
    }
    
    if(theBytes.length == 0) throw new WCException("Could not capture image");
    
    return theBytes;
  }
  
  public BufferedImage flip(BufferedImage anImage){
    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
    tx.translate(0, -anImage.getHeight(null));
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return op.filter(anImage, null);
  }
  
  public boolean isFlip() {
    return isFlip;
  }

  public void setFlip( boolean aFlip ) {
    isFlip = aFlip;
  }



  private class VideoFrameProcessor implements IVideoFrameProcessor{
    private int myCounter = 0;
    private final ArrayBlockingQueue<byte[]> myQueue;
    
    public VideoFrameProcessor( ArrayBlockingQueue<byte[]> aQueue ) {
      super();
      myQueue = aQueue;
    }

    @Override
    public void onFrame( int width, int height, byte[] rgb, int aArg3 ) {
      //the first frame is black
      if(++myCounter == 2){
        try {
          myQueue.add(createJPG(rgb, width, height));
        } catch (IOException e) {
          myQueue.add(new byte[0]);
        }
        myCaptureDevice.stopVideoCapture();
      }
      
    }
    
  }
}
