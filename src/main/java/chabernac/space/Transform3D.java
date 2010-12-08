package chabernac.space;

import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

public interface Transform3D{
  public Point2D transform(Point3D aPoint);
}