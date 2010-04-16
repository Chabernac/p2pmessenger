package chabernac.synchro;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.synchro.event.FireEvent;
import chabernac.synchro.event.LocationChangedEvent;

public abstract class SynchronizedPanel extends JPanel implements iEventListener{
  private int myPlayer;
  
  public SynchronizedPanel(String aServer, int aPort, int aPlayer){
    myPlayer = aPlayer;
    initEventListener(aServer, aPort);
    addMouseListener();
  }
  
  private void addMouseListener(){
    MyMouseListener theListener = new MyMouseListener();
    addMouseMotionListener(theListener);
    addMouseListener(theListener);
  }
  
  private void initEventListener(String aServer, int aPort){
    SynchronizedEventListener.connectToServer(aServer, aPort);
    ApplicationEventDispatcher.addListener(this);
  }

  public void eventFired(Event anEvt) {
    if(anEvt instanceof LocationChangedEvent){
      LocationChangedEvent theEvent = (LocationChangedEvent)anEvt;
      setPlayerLocation(theEvent.getPlayer(), theEvent.getX(), theEvent.getY());
    } else if(anEvt instanceof FireEvent){
      FireEvent theEvent = (FireEvent)anEvt;
      playerFired(theEvent.getPlayer(), theEvent.getX(), theEvent.getY(), theEvent.isFiring());
    }
  }

  protected abstract void setPlayerLocation(int anPlayer, int anX, int anY);
  protected abstract void playerFired(int anPlayer, int anX, int anY, boolean isFiring);

  private class MyMouseListener implements MouseMotionListener, MouseListener{

    public void mouseDragged(MouseEvent anE) {
      ApplicationEventDispatcher.fireEvent(new LocationChangedEvent(myPlayer, anE.getX(), anE.getY()));
    }

    public void mouseMoved(MouseEvent anE) {
      ApplicationEventDispatcher.fireEvent(new LocationChangedEvent(myPlayer, anE.getX(), anE.getY()));
    }

    public void mouseClicked(MouseEvent anE) {
      // TODO Auto-generated method stub
      
    }

    public void mouseEntered(MouseEvent anE) {
      // TODO Auto-generated method stub
      
    }

    public void mouseExited(MouseEvent anE) {
      // TODO Auto-generated method stub
      
    }

    public void mousePressed(MouseEvent anE) {
      ApplicationEventDispatcher.fireEvent(new FireEvent(myPlayer, anE.getX(), anE.getY(), true));
    }

    public void mouseReleased(MouseEvent anE) {
      ApplicationEventDispatcher.fireEvent(new FireEvent(myPlayer, anE.getX(), anE.getY(), false));
    }
    
  }
}
