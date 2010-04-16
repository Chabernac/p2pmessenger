package chabernac.utils;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.io.FileOutputStream;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;


public class JaiTools{
	public static PlanarImage loadImage(String imageName){
		ParameterBlock  pb = (new ParameterBlock()).add(imageName);
		PlanarImage src = JAI.create("fileload", pb);
		if(src == null){
			Debug.log(Tools.class,"Could not load image at: " + imageName);
		}
		return src;
	}

	public static PlanarImage scaleImage(PlanarImage aImage, int width, int height, boolean keepAspect){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(aImage);
		RenderableImage ren = JAI.createRenderable("renderable", pb);
		float scale;
		if(width > 0 && height > 0 && keepAspect){
			float xScale = aImage.getWidth() / (float)width;
			float yScale = aImage.getHeight() / (float)height;
			scale = Math.max(xScale, yScale);
			width = (int)(aImage.getWidth() / scale);
			height = (int)(aImage.getHeight() / scale);
		} else if(width > 0 && height == -1){
			scale = aImage.getWidth() / (float)width;
			height = (int)(aImage.getHeight() / scale);
		} else if(width == -1 && height > 0){
			scale = aImage.getHeight() / height;
			width = (int)(aImage.getWidth() / scale);
		}
		return (PlanarImage)ren.createScaledRendering(width, height, null);
	}

	public static PlanarImage useKernelOnImage(PlanarImage aImage, KernelJAI aKernel){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(aImage);
		pb.add(aKernel);
		return JAI.create("convolve", pb, null);
	}


	public static void encodeImage(PlanarImage img, FileOutputStream out, JPEGEncodeParam aParam){
		ImageEncoder theEncoder = ImageCodec.createImageEncoder("JPEG",out, aParam);
		try{
			theEncoder.encode(img);
			out.close();
		}catch(Exception e){
			Debug.log(Tools.class,"Could not encode image",e);
		}
	}
}