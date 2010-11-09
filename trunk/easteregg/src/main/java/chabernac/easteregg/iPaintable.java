package chabernac.easteregg;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface iPaintable {
  public void paint(Graphics g, Rectangle aBounds, BufferedImage aBackGround);
}
