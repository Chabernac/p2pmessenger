package chabernac.utils;

import java.util.*;
import java.io.*;
import chabernac.utils.Debug;

public class Preferences extends Hashtable implements Serializable
{

private File file = null;


public Preferences(File aFile)
{
	this.file = aFile;
}


public Preferences(String owner)
{
	this(new File(owner + ".bin"));
}

public File getFile(){return file;}

public void setPreference(String s,Object o){put(s,o);}

public Object getPreference(String s, Class preferenceClass)
{
	Object o = get(s);
	if(o == null)
	{
	try
	  {
		  Debug.log(this,"making new instance of: " + preferenceClass.toString());
		  o = preferenceClass.newInstance();
		  put(s,o);
	  }catch(Exception e)
		{
			Debug.log(this,"Exception occured while making new instance of " + preferenceClass.toString(),e);
			return null;
		}
	}
	return o;
}

public void removePreference(String s)
 {
	 remove(s);
 }

public static Preferences readPreferences(String owner)
{
	return Preferences.readPreferences(new File(owner + ".bin"));
}

public static Preferences readPreferences(File aFile)
{
	try
	  {
	  ObjectInputStream prefer = new ObjectInputStream(new FileInputStream(aFile));
	  return (Preferences)prefer.readObject();
  	  }catch(Exception e)
  	  	{
			Debug.log(Preferences.class,"Error reading preferences: " + aFile.toString() + "\n",e);
			return new Preferences(aFile);
		}
}

public void writePreferences()
{
try
	{
	ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
	output.writeObject(this);
	output.flush();
	output.close();
	}catch(Exception e) {Debug.log(this,"error writing preferences: ",e);}
}

}