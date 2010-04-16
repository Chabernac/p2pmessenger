package chabernac.utils;

public interface StatusListener
{
	public static String RUNNING="RUNNING";
	public static String STOPPED="STOPPED";

	public void statusChanged(Object o, String status);
}