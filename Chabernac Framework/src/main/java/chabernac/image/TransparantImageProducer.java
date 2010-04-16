package chabernac.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.Vector;

public class TransparantImageProducer {
	private Vector myColors = null;
	private TransparentRGBFilter myFilter = null;
	
	public TransparantImageProducer(){
		myColors = new Vector();
		myFilter = new TransparentRGBFilter();
	}
	
	public void addColor(Color aColor){
		myColors.addElement(aColor);
	}
	
	public Image makeTransparant(Image anImage){
		ImageProducer ip = new FilteredImageSource(anImage.getSource(), myFilter);
        return Toolkit.getDefaultToolkit().createImage(ip);
		
	}
	
	private class TransparentRGBFilter extends RGBImageFilter{

		public int filterRGB(int x, int y, int rgb) {
			for(int i=0;i<myColors.size();i++){
				int markerRGB = ((Color)myColors.elementAt(i)).getRGB() | 0xFF000000;
				if((rgb | 0xFF000000) == markerRGB) return 0x00FFFFFF & rgb;
			}
			return rgb;
		}
		
	}
}
