package chabernac.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import chabernac.queue.Queue;



public class Explorer
{
	private static String name = null;
	private static String extension = null;
	private static boolean selectName = false;
	private static boolean selectExtension = false;

  public synchronized static void findFiles(File root, boolean searchSubdir, String selection, Queue putQueue)
  {
	try
	{
	splitSelection(selection);

	Vector dirsFound = new Vector(100,100);

    int currentDir = 0;
    int currentFile = 0;

    if(root.isDirectory()){dirsFound.addElement(root);}
    else {putQueue.put(root);}


    File current = null;
    for(int i=0;i<dirsFound.size();i++)
    {
      current = (File)dirsFound.elementAt(i);
      File[] files = current.listFiles();
      for(int j=0;j<files.length;j++)
      {
        if(files[j].isDirectory() && searchSubdir)
        {
      		dirsFound.addElement(files[j]);
        }
        else
        {
		  if(matchSelection(files[j],selection)){putQueue.put(files[j]);}
        }
      }

    }

	}catch(Exception e){Debug.log(Explorer.class,"Exception occured while searching for files",e);}
   }

  private static void splitSelection(String selection)
  {
	  StringTokenizer tokenizer = new StringTokenizer(selection,".");
	  if(tokenizer.countTokens() == 2)
	  {
		  name = tokenizer.nextToken();
		  extension = "." + tokenizer.nextToken();
	  }
	  if(name.equals("*")){selectName = false;}
	  else{selectName = true;}
	  if(extension.equals("*")){selectExtension = false;}
	  else{selectExtension = true;}
  }

  private static boolean matchSelection(File aFile, String selection)
  {
	  String file = aFile.toString();
	  if(selectExtension && !file.endsWith(extension)){return false;}
	  if(selectName && file.indexOf(name)==-1){return false;}
	  return true;
  }

  public static File copyFile(File source, File destination)
  {
	  File theDestination = destination;
	  if(destination.isDirectory())
	  {
		  theDestination = new File(destination.toString() + "\\" + source.getName());
	  }
	  FileInputStream sourceInputStream = null;
	  FileOutputStream destinationOutputStream = null;
	  try
	  {
		  sourceInputStream = new FileInputStream(source);
		  destinationOutputStream = new FileOutputStream(theDestination);
		  int bytesRead = -1;
		  byte bytes[] = new byte[1024];

		  while((bytesRead = sourceInputStream.read(bytes)) != -1)
		  {
			destinationOutputStream.write(bytes, 0, bytesRead);
		  }
	  }catch(Exception e)
	  {
		  Debug.log(Explorer.class,"Error occured while copying files",e);
		  return null;
	  }
	   finally
	   {
		   try
		   {
			   if(sourceInputStream != null){sourceInputStream.close();}
			   if(destinationOutputStream != null)
			   {
				   destinationOutputStream.flush();
				   destinationOutputStream.close();
			   }
		   }catch(Exception e){Debug.log(Explorer.class,"Could not close streams",e);}
	   }
	 return theDestination;
  }
}