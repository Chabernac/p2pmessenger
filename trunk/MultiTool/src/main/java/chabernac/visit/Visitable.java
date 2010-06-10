package chabernac.visit;

public class Visitable {
  public void accept(iVisitor aVisitor){
    aVisitor.visit(this);
  }
}
