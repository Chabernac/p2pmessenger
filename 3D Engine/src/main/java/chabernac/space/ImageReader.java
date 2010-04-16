/*
 * Created on 11-feb-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import java.awt.Graphics;
import java.awt.Image;

import chabernac.squeue.TriggeredQueueReader;
import chabernac.squeue.TriggeringQueue;
import chabernac.squeue.iObjectProcessor;

public class ImageReader extends TriggeredQueueReader {

	public ImageReader(TriggeringQueue queue, Graphics aGraphics) {
		super(queue, new ImageProcessor(aGraphics));
	}


	private static class ImageProcessor implements iObjectProcessor{
		private Graphics myGraphics = null;
		
		public ImageProcessor(Graphics anImage){
			myGraphics = anImage;
		}
				
		
		public void processObject(Object anObject) {
			if(!(anObject instanceof Image)){
				return;
			}
			System.out.println("Drawing image");
			myGraphics.drawImage((Image)anObject, 0,0,null);
		}

	}
}
