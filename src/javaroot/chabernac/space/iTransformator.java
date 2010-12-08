package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public interface iTransformator {
  public Point3D transform(Point3D aPoint);
  public Point3D inverseTransform(Point3D aPoint);
  public GVector transform(GVector aVector);
  public GVector inverseTransform(GVector aVector);
  public Vertex transform(Vertex aVertex);
  public Vertex inverseTransform(Vertex aVertex);
}
