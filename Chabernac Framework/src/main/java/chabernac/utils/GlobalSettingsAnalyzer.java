package chabernac.utils;

import javax.swing.JOptionPane;

public abstract class GlobalSettingsAnalyzer
{
	public static void analyze()
	{
		if(!isValidUserName((String)GlobalSettings.storedSettings.get("User"))){askUserName();}
	}

	private static void askUserName()
	{
		String userName = null;

		userName = JOptionPane.showInputDialog("What is your name?");
		while(!isValidUserName(userName))
		{
			JOptionPane.showMessageDialog(null, "This user name sucks, please give another one!", "Stop", JOptionPane.ERROR_MESSAGE);
			userName = JOptionPane.showInputDialog("What is your name?");
		}
		GlobalSettings.storedSettings.put("User",userName);
	}

	private static boolean isValidUserName(String userName)
	{
		if(userName!=null && !userName.equals("") && userName.indexOf(" ")==-1 && userName.length() <= 10)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}