package chabernac.utils;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

public class WebMethod
{
	public static String LOCATION = "http://www.mycgiserver.com/servlet/powerscout.";
	//public static String LOCATION = "http://localhost:8080/sendmail/servlet/powerscout.";


	public static void logon(String user)
	{
		Hashtable params = new Hashtable();
		params.put("method","logon");
		params.put("user",user);
		//params.put("ip",ip);
		useMethod(LOCATION + "WebMethods?", params);
	}

	public static void logoff(String user)
	{
		Hashtable params = new Hashtable();
		params.put("method","logoff");
		params.put("user",user);
		useMethod(LOCATION + "WebMethods?", params);
	}


	public static void mail(String from, String to, String subject, String message)
	{
		Hashtable params = new Hashtable();
		params.put("from",from);
		params.put("to",to);
		params.put("subject",subject);
		params.put("message",message);
		useMethod(LOCATION + "SendMail?", params);
	}

	public static void useMethod(String location, Hashtable params)
	{
		Enumeration theKeys = params.keys();
		String key = null;
		while(theKeys.hasMoreElements())
		{
			key = (String)theKeys.nextElement();
			location = location + key + "=" + params.get(key) + "&";
		}
		try
		{
			URL url = new URL(Converter.convertToUrlString(location));
			UrlReader reader = new UrlReader(url);
			reader.start();
		}catch(Exception e){Debug.log(WebMethod.class,"Error occured while starting urlExecutor: " + e);}
	}


}


