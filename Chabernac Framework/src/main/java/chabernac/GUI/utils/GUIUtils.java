package chabernac.GUI.utils;

import java.awt.*;
import javax.swing.*;
import java.util.Vector;
import chabernac.utils.Debug;
import java.awt.event.*;

public class GUIUtils
{
public static void addMyComponent(Container parent,GridBagLayout layout, int anchor, Insets insets, int direction, int weigthx, int weigthy, int gridx, int gridy, int gridwidth, int gridheight, Component component,Color foreground)
{
	addMyComponent(parent,layout,anchor,insets,direction,(double)weigthx,(double)weigthy,gridx,gridy,gridwidth,gridheight,component,foreground);
}

public static void addMyComponent(Container parent,GridBagLayout layout, int anchor, Insets insets, int direction, double weigthx, double weigthy, int gridx, int gridy, int gridwidth, int gridheight, Component component,Color foreground)
{
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.anchor = anchor;
	constraints.insets = insets;
	constraints.fill = direction;
	constraints.weightx = weigthx;
	constraints.weighty = weigthy;
	constraints.gridx = gridx;
	constraints.gridy= gridy;
	constraints.gridwidth = gridwidth;
	constraints.gridheight = gridheight;

	if(foreground!=null){component.setForeground(foreground);}
	//component.setBackground(Color.red);
	layout.setConstraints(component,constraints);
	parent.add(component);
}

public static JMenuBar buildMenu(Vector menuItems, ActionListener aActionListener)
{
	JMenuBar theMenuBar = new JMenuBar();
	for(int i=0;i<menuItems.size();i++)
	{
		Debug.log(GUIUtils.class,"Adding JMenu");
		theMenuBar.add(buildJMenu((Vector)menuItems.elementAt(i),aActionListener));
	}
	return theMenuBar;
}

public static JMenu buildJMenu(Vector menuItems, ActionListener aActionListener)
{
	JMenu theMenu = new JMenu((String)menuItems.elementAt(0));
	for(int i=1;i<menuItems.size();i++)
	{
		if(menuItems.elementAt(i) instanceof Vector)
		{
			Debug.log(GUIUtils.class,"Adding JMenu");
			theMenu.add(buildJMenu((Vector)menuItems.elementAt(i),aActionListener));
		}
		else if(menuItems.elementAt(i) instanceof String)
		{
			Debug.log(GUIUtils.class,"Adding JMenuItem");
			JMenuItem theItem = new JMenuItem((String)menuItems.elementAt(i));
			theItem.addActionListener(aActionListener);
			theMenu.add(theItem);
		}
		else
		{
			theMenu.add((Component)menuItems.elementAt(i));
		}

	}
	return theMenu;
}



    // Used to identify the windows platform.
    private static final String WIN_ID = "Windows";
    // The default system browser under windows.
    private static final String WIN_PATH = "rundll32";
    // The flag to display a url.
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    // The default browser under unix.
    private static final String UNIX_PATH = "netscape";
    // The flag to display a url.
    private static final String UNIX_FLAG = "-remote openURL";


public static void displayURL(String url)
    {
        boolean windows = isWindowsPlatform();
        String cmd = null;
        try
        {
            if (windows)
            {
                // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                Process p = Runtime.getRuntime().exec(cmd);
            }
            else
            {
                // Under Unix, Netscape has to be running for the "-remote"
                // command to work.  So, we try sending the command and
                // check for an exit value.  If the exit command is 0,
                // it worked, otherwise we need to start the browser.
                // cmd = 'netscape -remote openURL(http://www.javaworld.com)'
                cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
                Process p = Runtime.getRuntime().exec(cmd);
                try
                {
                    // wait for exit code -- if it's 0, command worked,
                    // otherwise we need to start the browser up.
                    int exitCode = p.waitFor();
                    if (exitCode != 0)
                    {
                        // Command failed, start up the browser
                        // cmd = 'netscape http://www.javaworld.com'
                        cmd = UNIX_PATH + " "  + url;
                        p = Runtime.getRuntime().exec(cmd);
                    }
                }
                catch(InterruptedException x)
                {
                    System.err.println("Error bringing up browser, cmd='" +
                                       cmd + "'");
                    System.err.println("Caught: " + x);
                }
            }
        }
        catch(java.io.IOException x)
        {
            // couldn't exec browser
            Debug.log(GUIUtils.class,"Could not invoke browser, command=" + cmd,x);
        }
    }

 public static boolean isWindowsPlatform()
	    {
	        String os = System.getProperty("os.name");
	        if ( os != null && os.startsWith(WIN_ID))
	            return true;
	        else
	            return false;

	    }
}