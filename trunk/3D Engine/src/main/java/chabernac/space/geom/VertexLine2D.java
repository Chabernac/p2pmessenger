/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.geom;

public class VertexLine2D {
  private final Vertex2D myStart;
  private final Vertex2D myEnd;
  private final int myColor;
  
  public VertexLine2D( Vertex2D aStart, Vertex2D aEnd, int aColor ) {
    super();
    myStart = aStart;
    myEnd = aEnd;
    myColor = aColor;
  }
  public Vertex2D getStart() {
    return myStart;
  }
  public Vertex2D getEnd() {
    return myEnd;
  }
  public int getColor() {
    return myColor;
  }
}
