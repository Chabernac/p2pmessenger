package chabernac.pie;

public class DefaultPiece implements Piece {
  private String myName = null;
  private double myWeight;
  
  public DefaultPiece(String aName, double aWeight){
    myName = aName;
    myWeight = aWeight;
  }

  public String getName() {
    return myName;
  }

  public void setName(String anName) {
    myName = anName;
  }

  public double getWeight() {
    return myWeight;
  }

  public void setWeight(double anWeight) {
    myWeight = anWeight;
  }
}
