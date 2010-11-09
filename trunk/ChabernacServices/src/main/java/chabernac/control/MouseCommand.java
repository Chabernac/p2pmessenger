
package chabernac.control;

public interface MouseCommand {
  public void mouseMoved(float aSpeedX, float aSpeedY);
  public void mouseRightClicked();
  public void mouseLeftClicked();
  public void mouseMidClicked();
  public void mouseScrollUp();
  public void mouseScrollDown();
}
