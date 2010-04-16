package chabernac.server;

import chabernac.utils.Debug;

public class EchoObjectProtocol extends DefaultObjectProtocol
{
	public void handle()
	{
		try
		{
			send("Hello");
			Object aObject;
			String cmd = "";
			while(!cmd.equals("QUIT"))
			{
				aObject = receive();
				if(aObject instanceof java.lang.String)
				{
					Debug.log(this,"Command received: " + cmd);
					cmd = (String)aObject;
					send(cmd);
				}
			}
		}catch(Exception e){Debug.log(this,"Error occured in handle of EchoProtocol",e);}
	}

}