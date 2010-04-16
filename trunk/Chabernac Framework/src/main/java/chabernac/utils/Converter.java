package chabernac.utils;

import java.util.Vector;
import java.util.StringTokenizer;

public class Converter
{
	public static Vector convertStringToVector(String aString, String aSeperation)
	{
		Vector theVector = new Vector(1,2);
		StringTokenizer theTokenizer = new StringTokenizer(aString,aSeperation);
		while(theTokenizer.hasMoreElements())
			{
				theVector.addElement(theTokenizer.nextElement());
			}
		return theVector;
	}

	public static String convertVectorToString(Vector aVector, String aSeperation)
	{
		String theString = "";
		for(int i=0;i<aVector.size();i++)
			{
				if(i==0){theString = (String)aVector.elementAt(0);}
				else
				{
				theString = theString + aSeperation + (String)aVector.elementAt(i);
				}
			}
		return theString;
	}

	public static String convertToUrlString(String aString)
	{
		String newString = "";
		StringTokenizer tokenizer = new StringTokenizer(aString);
		while(tokenizer.hasMoreTokens())
		{
			newString = newString + tokenizer.nextElement() + "%20";
		}
		return newString;
	}

}