/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import chabernac.image.ImageFactory;

public class TextureFactory {
  private static HashMap MAP = new HashMap();
  private static HashMap BUMP_MAP = new HashMap();

  public static TextureImage getTexture(String aTexture, boolean isTransparent) throws IOException{
    if(!MAP.containsKey(aTexture)){
      BufferedImage theImage = ImageFactory.loadImage(aTexture, isTransparent);

      TextureImage theTextureImage = new TextureImage(theImage);
      MAP.put(aTexture, theTextureImage);
    }
    return (TextureImage)MAP.get(aTexture);
  }

  public static BumpMap getBumpMap(String aBumpMap) throws IOException{
    if(!BUMP_MAP.containsKey(aBumpMap)){
      BufferedImage theImage = ImageFactory.loadImage(aBumpMap, false);
      BumpMap theBumpMap = new BumpMap(new TextureImage(theImage), 10D);
      BUMP_MAP.put(aBumpMap, theBumpMap);
    }
    return (BumpMap)BUMP_MAP.get(aBumpMap);
  }
}
