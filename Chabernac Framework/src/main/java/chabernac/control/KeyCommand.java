package chabernac.control;

public abstract class KeyCommand{
  private String myDescription = null;
  
  public KeyCommand(String aDescription){
    myDescription = aDescription;
  }
  
  public void setDescription(String aDescription){ myDescription = aDescription; }
  public String getDescription(){ return myDescription; }
  
  public abstract void keyPressed();
  public abstract void keyDown();
  public abstract void keyReleased();
}