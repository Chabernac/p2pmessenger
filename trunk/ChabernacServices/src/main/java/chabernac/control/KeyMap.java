package chabernac.control;

import java.util.Observable;

public class KeyMap extends Observable{
  private int myKeyCodes[];
  private KeyCommand myKeyPressedCommand;
  
  public KeyMap(KeyCommand aCommand){
    this(-1, aCommand, 1);
  }
  
  public KeyMap(int aKeyCode, KeyCommand aCommand){
    this(aKeyCode, aCommand, 1);
  }
  
  public KeyMap(KeyCommand aCommand, int aMaxMappings){
    this(-1, aCommand, aMaxMappings);
  }
  
  public KeyMap(int aKeyCode, KeyCommand aCommand, int aMaxMappings){
    myKeyCodes = new int[aMaxMappings];
    if(aKeyCode != -1) myKeyCodes[0] = aKeyCode;
    myKeyPressedCommand = aCommand;
  }
  
  public KeyMap(int[] aKeyCodes, KeyCommand aCommand){
    myKeyCodes = aKeyCodes;
    myKeyPressedCommand = aCommand;
  }
  
  public int[] getKeyCodes(){ return myKeyCodes; }
  public void setKey(int aKeyCode, int aIndex) throws ArrayIndexOutOfBoundsException{ myKeyCodes[aIndex] = aKeyCode; }
  public KeyCommand getCommand(){ return myKeyPressedCommand; }
  public void setKeyCommand(KeyCommand aCommand){ myKeyPressedCommand = aCommand; }
}