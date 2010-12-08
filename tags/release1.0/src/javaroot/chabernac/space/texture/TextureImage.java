/*
 * Created on 18-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.awt.image.BufferedImage;

public class TextureImage{
	public BufferedImage image = null;
	public int width = 0;
	public int height = 0;
	public int[] colors;

	public TextureImage(BufferedImage anImage){
		image = anImage;
		width = image.getWidth();
		height = image.getHeight();
		createBuffer();
	}
	
	private void createBuffer(){
		colors = new int[width * height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				colors[y * width + x] = image.getRGB(x, y);
			}
		}
	}
	
	public int getColorAt(int x, int y){
		while(x < 0) x += width;
		while(x >= width) x -= width;
		while(y < 0) y += height;
		while(y >= height) y -= height;
		
		return colors[y * width + x];
	}

}


