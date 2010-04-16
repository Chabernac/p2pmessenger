package chabernac.synchro;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleSynchronizedPanel extends SynchronizedPanel {
  private static final long serialVersionUID = 7201008094068766278L;
  private Map myPlayerList = null;
  
  public SimpleSynchronizedPanel(String anServer, int anPort, int anPlayer) {
    super(anServer, anPort, anPlayer);
    myPlayerList = new HashMap();
  }

  protected void setPlayerLocation(int aPlayer, int anX, int anY) {
    Player thePlayer = getPlayer(aPlayer);
    thePlayer.setX(anX);
    thePlayer.setY(anY);
    repaint();
  }
  
  protected void playerFired(int aPlayer, int anX, int anY, boolean isFiring) {
    Player thePlayer = getPlayer(aPlayer);
    thePlayer.setFiring(isFiring);
    repaint();
  }
  
  
  private Player getPlayer(int aPlayer){
    Integer thePlayerId = new Integer(aPlayer);
    if(!myPlayerList.containsKey(thePlayerId)){
      Player thePlayer = new Player();
      thePlayer.setId(aPlayer);
      myPlayerList.put(thePlayerId, thePlayer);
    }
    
    return (Player)myPlayerList.get(thePlayerId);
  }
  
  public void paint(Graphics g){
    g.setColor(Color.white);
    g.fillRect(0, 0, getWidth(), getHeight());
    for(Iterator i=myPlayerList.values().iterator();i.hasNext();){
      Player thePlayer = (Player)i.next();
      if(thePlayer.isFiring){
        g.setColor(Color.red);
      } else {
        g.setColor(Color.black);
      }
      g.fillOval(thePlayer.getX() - 4,  thePlayer.getY() - 4, 8, 8);
    }
  }
  
  private class Player{
    private int id;
    private int x;
    private int y;
    private boolean isFiring = false;
    
    public int getId() {
      return id;
    }
    public void setId(int anId) {
      id = anId;
    }
    public int getX() {
      return x;
    }
    public void setX(int anX) {
      x = anX;
    }
    public int getY() {
      return y;
    }
    public void setY(int anY) {
      y = anY;
    }
    public boolean isFiring() {
      return isFiring;
    }
    public void setFiring(boolean anIsFiring) {
      isFiring = anIsFiring;
    }
    
  }
}
