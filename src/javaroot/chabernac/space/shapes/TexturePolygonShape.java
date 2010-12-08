/*
 * Created on 22-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.shapes;

import java.io.IOException;

import org.apache.log4j.Logger;

import chabernac.space.geom.Point3D;
import chabernac.space.texture.TextureFactory;
import chabernac.space.texture.TextureImage;

public class TexturePolygonShape extends SinglePolygonShape {
	private static Logger LOGGER = Logger.getLogger(TexturePolygonShape.class);
	
	public TexturePolygonShape(String aTexture, boolean isTransparent, Point3D anOrigin) {
		super(anOrigin);
		try{
			TextureImage theImage = TextureFactory.getTexture(aTexture, isTransparent);
			setWidth(theImage.width);
			setHeight(theImage.height);
			createPolygons();
			setTexture(aTexture);
		}catch(IOException e){
			LOGGER.error("Unable to load texture image", e);
		}
	}
	
}
