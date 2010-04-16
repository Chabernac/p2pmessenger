package chabernac.data;

import chabernac.geom.Location;

public interface ShiftListListener{
	public void shiftListChanged(Location aSourceLocation, Location aDestinationLocation, Object aObject);
}