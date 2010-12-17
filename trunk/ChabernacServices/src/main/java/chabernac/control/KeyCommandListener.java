
package chabernac.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

public class KeyCommandListener implements KeyListener, Observer{

  private KeyCommand[] myKeyCommands;
  private KeyMapContainer myKeyMapContainer;


  public KeyCommandListener(KeyMapContainer aKeyMapContainer){
    initialize();
    myKeyMapContainer = aKeyMapContainer;
    myKeyMapContainer.addObserver(this);
    rebuild();
  }

  private void initialize(){
    myKeyCommands = new KeyCommand[255];
  }

  public void buildMap(KeyMapContainer keyMappings){
    clearMap();
    for(int i=0;i<keyMappings.size();i++){
      for(int j=0;j<keyMappings.keyMapAt(i).getKeyCodes().length;j++){
        myKeyCommands[keyMappings.keyMapAt(i).getKeyCodes()[j]] = keyMappings.keyMapAt(i).getCommand(); 
      }
    }
  }

  private void clearMap(){
    for(int i=0;i<myKeyCommands.length;i++){
      myKeyCommands[i] = null;
    }
  }

  public void rebuild(){
    buildMap(myKeyMapContainer);
  }

  public void keyPressed(KeyEvent e){ 
    if(myKeyCommands[e.getKeyCode()] != null) myKeyCommands[e.getKeyCode()].keyPressed(); 
  }

  public void keyReleased(KeyEvent e){ 
    if(myKeyCommands[e.getKeyCode()] != null) myKeyCommands[e.getKeyCode()].keyReleased(); 
  }

  public void keyTyped(KeyEvent e) {}

  public void update(Observable arg0, Object arg1) {
    rebuild();
  }

}
