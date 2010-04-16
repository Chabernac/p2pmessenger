package chabernac.utils;

import java.util.Date;
import chabernac.GUI.components.*;

public class Debug
{
	private static boolean debug = false;
	private static Class[] debugClass = null;
	private static Date currentDate = new Date();

	public static void log(Object o, String s)
	{
		if(debug)
		{
			Class theClass = null;
			if(!(o instanceof java.lang.Class)){
				theClass = o.getClass();
			} else {
				theClass = (Class)o;
			}
			if(isDebugClass(theClass))
			{
				System.out.println((currentDate = new Date()).toString() + " " +  o.toString() + ":" + s);
			}
		}

	}

	public static void log(Object o, String s, Throwable e, boolean show)
	{
		System.err.println((currentDate = new Date()).toString() + " Exception in object: " + o.toString() + "\n" + s);
		e.printStackTrace();
		if(show)
		{
			new ErrorWindow(e).show();
		}
	}

	public static void log(Object o, String s, Throwable e)
	{
		Debug.log(o,s,e,false);
	}

	public static void log(Object o, String s, Exception e)
	{
		System.err.println((currentDate = new Date()).toString() + " Exception in object: " + o.toString() + "\n" + s);
		e.printStackTrace();
	}

	public static void setDebug(boolean d)
	{
		Debug.log(Debug.class,"Debug set to " + d);
		debug = d;
	}
	public static boolean isDebug(){return debug;}

	public static void setDebugClasses(Class[] debugCl){debugClass = debugCl;}

	private static boolean isDebugClass(Class aClass)
	{
		if(debugClass == null){return true;}
		for(int i=0;i<debugClass.length;i++)
		{
			if(debugClass[i].equals(aClass))
			{
				return true;
			}

		}
		return false;
	}
}