package chabernac.pie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pie {
  private List myPieces = null;
  private double myTotal = 0D;
  private String myName = "";
  
  public Pie(){
    myPieces = new ArrayList();
  }
  
  public void addPiece(Piece aPiece){
    myPieces.add(aPiece);
    myTotal += aPiece.getWeight();
  }
  
  public void removePiece(Piece aPiece){
    myPieces.remove(aPiece);
    myTotal -= aPiece.getWeight();
  }
  
  public double getWeight(Piece aPiece){
    if(!myPieces.contains(aPiece)){
      return 0D;
    } else {
      return aPiece.getWeight() / myTotal;
    }
  }
  
  public List getPieces(){
    return Collections.unmodifiableList(myPieces);
  }
  
  public static void main(String args[]){
    Pie thePie = new Pie();
    Piece thePiece = new DefaultPiece("1", 5);
    thePie.addPiece(thePiece);
    Piece thePiece2 = new DefaultPiece("2", 20);
    thePie.addPiece(thePiece2);
    Piece thePiece3 = new DefaultPiece("3", 10);
    thePie.addPiece(thePiece3);
    System.out.println("weight: " + thePie.getWeight(thePiece2));
  }
  
  public void clear(){
    myPieces.clear();
    myTotal = 0;
  }

  public String getName() {
    return myName;
  }

  public void setName(String anName) {
    myName = anName;
  }
  
}
