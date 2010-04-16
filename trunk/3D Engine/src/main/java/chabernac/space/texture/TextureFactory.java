/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureFactory {


	public static BufferedImage getTexture(String aTexture) throws IOException{
		return ImageIO.read(new File("D:\\Projects\\JAVA\\3D Engine\\textures\\" + aTexture + ".jpg"));
	}

}
