package chabernac.chat.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class ImageWindow extends JDialog{
	private Image myImage = null;
	private Image myOtherImage = null;
	
	private static final int OFFSET = 30;
	
	public ImageWindow(Image anImage, Image anotherImage, int aPercentage){
		super((JFrame)null, true);
		setTitle(aPercentage + " %");
		myImage = anImage;
		myOtherImage = anotherImage;
		setSize(myImage.getWidth(null), myImage.getHeight(null) + myOtherImage.getHeight(null) + OFFSET + 1);
		setVisible(true);
	}
	
	public void paint(Graphics g){
		super.paintComponents(g);
		g.drawImage(myImage, 0, OFFSET, null);
		g.drawLine(0, myImage.getHeight(null) + OFFSET, myImage.getWidth(null), myImage.getHeight(null) + OFFSET);
		g.drawImage(myOtherImage, 0, myImage.getHeight(null) + OFFSET + 1, null);
	}

}
