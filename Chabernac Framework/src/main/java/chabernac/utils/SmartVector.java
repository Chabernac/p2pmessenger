package chabernac.utils;

import java.util.Vector;

public class SmartVector extends Vector
{
	public SmartVector(){super();}
	public SmartVector(int a, int b){super(a,b);}
	public SmartVector(int a){super(a);}

	public void addVector(Vector v)
	{
		for(int i=0;i<v.size();i++)
		{
			addElement(v.elementAt(i));
		}
	}

	public boolean addSmart(Object aObject){
		for(int i=0;i<size();i++){
			if(elementAt(i).equals(aObject)){
				return false;
			}
		}
		addElement(aObject);
		return true;
	}
}
