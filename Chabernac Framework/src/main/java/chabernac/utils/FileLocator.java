package chabernac.utils;
import java.io.*;
import chabernac.queue.*;

public class FileLocator extends Thread
{
	private File root = null;
	private boolean searchSubdir;
	private String selection = null;
	private Queue putQueue = null;

	public FileLocator(File root, boolean searchSubdir, String selection, Queue putQueue)
	{
			this.root = root;
			this.searchSubdir = searchSubdir;
			this.selection = selection;
			this.putQueue= putQueue;
			new Thread(this).start();
	}


/*
	public synchronized static void findFiles(File root, boolean searchSubdir, String selection, Queue putQueue)
  	{
		this.root = root;
		this.searchSubDir = searchSubDir;
		this.selection = selection;
		this.putQueue= putQueue;
		new Thread(this).start();
	}
	*/

	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);
		Explorer.findFiles(root,searchSubdir,selection,putQueue);
	}
}
