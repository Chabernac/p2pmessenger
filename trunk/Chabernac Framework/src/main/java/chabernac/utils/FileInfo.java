package chabernac.utils;

import java.io.*;

public class FileInfo implements Serializable
{
	File myFile = null;
	String fileName = null;
	String fileNameNoExtension = null;
	String extension = null;
	String path = null;
	String fullPath = null;
	String comment = "";

	public FileInfo(File aFile)
	{
		myFile = aFile;
		getInfo();
	}

	private void getInfo()
	{
		fullPath = myFile.toString();
		path = fullPath.substring(0, fullPath.lastIndexOf("\\"));
		fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1,fullPath.length());
		fileNameNoExtension = fileName.substring(0, fileName.lastIndexOf("."));
		extension = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
	}

	public File getFile(){return myFile;}
	public String getPath(){return path;}
	public String getFullPath(){return fullPath;}
	public String getFileName(){return fileName;}
	public String getFileNameNoExtension(){return fileNameNoExtension;}
	public String getExtension(){return extension;}
	public String getComment(){return comment;}
	public void setComment(String comment){this.comment = comment;}
	public File getParent(int level){
		File parentF = myFile;
		for(int j=0;j<level;j++){
				parentF = parentF.getParentFile();
		}
		return parentF;
	}
}
