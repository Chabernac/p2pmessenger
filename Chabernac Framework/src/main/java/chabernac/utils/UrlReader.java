package chabernac.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UrlReader extends Thread
{
	private URL url = null;

	public UrlReader(URL url)
	{
		this.url = url;
	}

	public void run()
	{
		BufferedReader theReader = null;
		try
			{
				Debug.log(this,"Executing url: " + url.toString());
				theReader = new BufferedReader(new InputStreamReader(url.openStream()));
				String theLine = null;
				while((theLine = (theReader.readLine()))!=null)
				{
					Debug.log(this,"Response from webmethod: " + theLine);
				}
			}catch(Exception e){Debug.log(this,"Error occured while executing url: " + e);}
	}

}