/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.web;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

/**
 * 
 * <code>JVideoScreen</code> - video screen class.
 */
public class JVideoScreen extends JComponent{

  private static final long serialVersionUID = 2397302975258684887L;

  /**
   * 
   * Returns default frame size.
   * 
   * @return default frame size
   */

  public final static Dimension getDefaultFrameSize(){
    return DEFAULT_FRAME_SIZE;
  }

  // introduced for the performance reasons
  /**
   * 
   * Default frame size.
   */
  private final static Dimension DEFAULT_FRAME_SIZE = new Dimension(320, 240);

  /**
   * 
   * Default color model.
   */

  private final static ColorModel DEFAULT_COLOR_MODEL =

  new ComponentColorModel(
  ColorSpace.getInstance(ColorSpace.CS_sRGB),
  new int[] { 8, 8, 8 },
  false, false,
  Transparency.OPAQUE,
  DataBuffer.TYPE_BYTE);
  /**
   * 
   * RGB bands offset
   */

  private final static int BAND_OFFSETS[] = new int[] {
  2 /* offset to Blue */,
  1 /* offset to Green */,
  0 /* offset to Red */
  }; // BAND_OFFSETS.length represents # of bands

  /**
   * 
   * <code>ZERO_POINT</code> = new Point(0,0)
   */

  private final static Point ZERO_POINT = new Point(0, 0);
  // fields
  // flag that controls whether or not the off-screen buffer is used
  private boolean useBuffer = true;
  // buffer for the rendered component
  private VolatileImage buffer = null;
  // height of the component buffer
  private int bufferHeight = 0;
  // width of the component buffer
  private int bufferWidth = 0;
  // frame
  private Image videoFrame = null;
  private Dimension frameSize = null;
  private ColorModel colorModel = DEFAULT_COLOR_MODEL;
  private SampleModel sampleModel = null;

  private Dimension screenSize = getDefaultFrameSize();
  // image affine transform
  private AffineTransform transform = null;
  // image transform
  private boolean flip = false;
  private double scale = 1;

  /**
   * 
   * Constructor.
   */

  public JVideoScreen(){
    this(false);
  }
  /**
   * 
   * Constructor.
   * 
   * 
   * 
   * @param flip
   *          set <code>true</code> to flip the frame
   */

  public JVideoScreen(final boolean flip){
    this(getDefaultFrameSize(), flip);
  }

  /**
   * 
   * Constructor.
   * 
   * 
   * 
   * @param size
   *          video frame size
   * 
   * @param flip
   *          set <code>true</code> to flip the frame
   */

  public JVideoScreen(final Dimension size, final boolean flip){
    setPreferredSize(getDefaultFrameSize());
    setFrameSize(size);
    flipFrame(flip);
  }

  /**
   * 
   * Returns the frame size.
   * 
   * 
   * 
   * @return frame size
   */

  public final Dimension getFrameSize(){
    return frameSize;
  }

  /**
   * 
   * Set <code>true</code> to flip video frame vertically.
   * 
   * @param flip
   *          flip parameter
   */

  public void flipFrame(final boolean flip){
    this.flip = flip;
    this.transform = createTransform(this.frameSize.height, this.flip, this.scale);
  }

  /**
   * Sets video scale.
   * 
   * @param scale
   *          scale parameter
   */

  public void setScale(final double scale){
    this.scale = scale;
    this.transform = createTransform(this.frameSize.height, this.flip, this.scale);
    // layout component if necessary
    setScreenSize(this.frameSize);
  }

  /**
   * 
   * Sets the frame to be rendered.
   * 
   * 
   * 
   * @param data
   *          frame data
   */

  public void setFrame(final byte[] data){
    setFrame(renderFrame(data));
  }

  /**
   * 
   * Sets still image to be shown.
   * 
   * @param image
   *          image to be shown
   */

  public void setImage(final Image image){
    setFrame(image);
  }

  /**
   * 
   * Sets the video frame.
   * 
   * 
   * 
   * @param videoFrame
   *          video frame
   */

  private void setFrame(final Image videoFrame){
    if (videoFrame == null)
      return;
    this.videoFrame = videoFrame;
    setFrameSize(this.videoFrame.getWidth(null), this.videoFrame.getHeight(null));
    repaint();
  }

  /**
   * 
   * Sets the frame size.
   * 
   * 
   * 
   * @param size
   *          a new screen size to be set
   */
  public void setFrameSize(final Dimension size){
    if (this.frameSize == size)
      return;
    this.frameSize = (size == null) ? getDefaultFrameSize() : size;
    // create sample model
    this.sampleModel = createSampleModel(this.frameSize);
    // create flip transform
    this.transform = (this.transform == null) ? null : createTransform(this.frameSize.height, this.flip, this.scale);
    this.setScreenSize(this.frameSize);
  }
  /**
   * 
   * Sets frame size.
   * 
   * @param width
   *          frame width
   * 
   * @param height
   *          frame height
   */

  public void setFrameSize(final int width, final int height){
    if ((frameSize.width != width && width > 0) ||
    (frameSize.height != height && height > 0)){
      setFrameSize(new Dimension(width, height));
    }
  }

  /**
   * 
   * Sets {@link ColorModel <tt>colorModel</tt>} to be used for
   * 
   * video frame rendering.
   * 
   * @param colorModel
   *          color model for frame rendering
   */
  public void setFrameColorModel(final ColorModel colorModel){
    this.colorModel = (colorModel == null) ? DEFAULT_COLOR_MODEL : colorModel;
  }

  // inner use methods

  @Override
  public void paintComponent(Graphics g){
    final int width = getWidth();
    final int height = getHeight();
    if (useBuffer){
      // do we need to resize the buffer?
      if ((buffer == null) || (bufferWidth != width) || (bufferHeight != height)){
        bufferWidth = width;
        bufferHeight = height;
        buffer = createBuffer();
      }
      do{
        switch (buffer.validate(getGraphicsConfiguration())) {
        case VolatileImage.IMAGE_INCOMPATIBLE:{
          buffer = createBuffer();
        } // no break here
        case VolatileImage.IMAGE_OK:
        case VolatileImage.IMAGE_RESTORED:{
          // render offscreen
          do{
            Graphics bg = buffer.createGraphics();
            render(bg, bufferWidth, bufferHeight);
            bg.dispose();
          } while (buffer.contentsLost());
        }
          break;
        }
        g.drawImage(buffer, 0, 0, this);
      }
      while (buffer.contentsLost());
    } else {
      render(g, width, height);
    }
  }

  @Override
  public void setVisible(final boolean flag){
    super.setVisible(flag);
  }

  @Override
  public void update(Graphics g){
    paint(g);
  }

  // inner use methods

  protected void render(Graphics g, final int width, final int height){
    final int x = (getBounds().width - screenSize.width) / 2;
    final int y = (getBounds().height - screenSize.height) / 2;
    final Graphics2D g2d = (Graphics2D) g;
    // draw background
    if (x != 0 || y != 0){
      g2d.setColor(getBackground());
      g2d.fillRect(0, 0, getBounds().width, getBounds().height);
    }

    if (videoFrame != null){
      g2d.drawImage(videoFrame, transform, null);
    }
  }

  /**
   * 
   * Creates buffer.
   * 
   * 
   * 
   * @return buffer image
   */

  private final VolatileImage createBuffer(){
    if (buffer != null)
      buffer.flush();
    return createVolatileImage(bufferWidth, bufferHeight);
  }

  /**
   * 
   * Sets screen size by frame size (screen size = frame size * scale).
   * 
   * @param size
   *          frame size
   */

  private void setScreenSize(final Dimension size) {
    final Dimension screenSize =
    new Dimension((int) (scale * size.width), (int) (scale * size.height));
    // update component preferred size
    final Dimension newPreferredSize =
    (screenSize.width > getDefaultFrameSize().width ||
    screenSize.height > getDefaultFrameSize().height) ?
    screenSize : getDefaultFrameSize();
    if (!screenSize.equals(newPreferredSize)){
      this.screenSize = screenSize;
      setMaximumSize(screenSize);
      setMinimumSize(screenSize);
      setPreferredSize(screenSize);
      setSize(screenSize);
      invalidate();
    }
    repaint();
  }

  /**
   * 
   * Renders video frame (@link BufferedImage) from frame data array,
   * 
   * frame size, color model.
   * 
   * @param data
   *          video frame data
   * 
   * @return video frame image
   */

  private BufferedImage renderFrame(final byte[] data){
    return new BufferedImage(colorModel,
    Raster.createWritableRaster(sampleModel, new DataBufferByte(data,
    frameSize.width * frameSize.height * 3, 0), ZERO_POINT),
    false, null);
  }

  /**
   * 
   * Creatres {@link SampleModel} for the video frame.
   * 
   * <p>
   * Note: Uses frame size, band offsets...
   * 
   * @return video frame sample model
   */

  private final static SampleModel createSampleModel(final Dimension size){
    return new PixelInterleavedSampleModel(
    DataBuffer.TYPE_BYTE /* data type for storing samples */,
    size.width /* width in pixels */,
    size.height /* height in pixels */,
    3 /* pixel stride */,
    size.width * 3 /* line stride */,
    BAND_OFFSETS /* the offsets of all bands */);
  }

  /**
   * 
   * Creates image {@link AffineTransform}.
   * 
   * @param frameHeight
   *          frame height
   * 
   * @param flip
   *          <code>true</code> to flip image
   * 
   * @param scale
   *          scale parameter
   * @return image affine transform
   */

  private static AffineTransform createTransform(
  final int frameHeight, final boolean flip, final double scale){
    AffineTransform transform = null;
    if (scale == 1){
      if (!flip)
        return null;
      transform = new AffineTransform();
      transform.translate(0, frameHeight);
      transform.scale(1, (flip ? -1 : 1));
    } else {
      transform = new AffineTransform();
      if (flip) transform.translate(0, scale * frameHeight);
      transform.scale(scale, (flip ? -1 : 1) * scale);
    }

    return transform;
  }
}