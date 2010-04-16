package chabernac.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;

import chabernac.server.Protocol;

public class MessageServer implements Runnable
{
	private boolean stopServer = false;
	private Protocol protocol = null;
	private int port = 0;

	public MessageServer(int port)
	{
		this.port = port;
		initialize();
	}

private void initialize()
{
	protocol = new DefaultProtocol();
}

public void run()
{
	ServerSocket server = null;
	try
	{
		System.out.println("Making server socket");
		server = new ServerSocket(port);
		while(!stopServer)
		{
			System.out.println("Waiting for client");
			new Thread(new SocketHandler(server.accept())).start();
		}
	}catch(Exception e){
		System.out.println("Server stopped: " + e);
		e.printStackTrace();
		}
     finally
     {
		 try
		 {
		 server.close();
	 	 }catch(Exception e){System.out.println("Error: " + e);}
	 }
}

public void stopServer(){stopServer = true;}
public void setProtocol(Protocol protocol){this.protocol = protocol;}


private class SocketHandler implements Runnable
{
	private Socket client = null;

	public SocketHandler(Socket client)
	{
		this.client = client;
	}

	public void run()
	{
		BufferedReader in = null;
		PrintWriter out  = null;
		try
		{
		System.out.println("Client accepted");
		if(protocol!=null){protocol.handle(client.getInputStream(),client.getOutputStream());}

		}catch(Exception e)
			{
				System.out.println("Socket closed: " + e);
	 		}
	 	 finally
	 	 	{
				try
				{
			 		in.close();
					out.close();
					client.close();
				}catch(Exception e){System.out.println("Error: " + e);}
			}

	}
}

private class DefaultProtocol implements Protocol
{
	public void handle(InputStream in, OutputStream out)
		{
//			String line = "";
//			try
//			{
//
//				while(!(line = in.readLine()).toUpperCase().equals("QUIT"))
//				{
//					System.out.println(line);
//				}
//			}catch(Exception e){Debug.log(this,"processIO stopped",e);}
		}

}

public static void main(String args[])
{

	JFrame frame = new JFrame();
	frame.setSize(100,100);
	frame.show();
	frame.setVisible(false);


	Vector servers = new Vector(10,10);
	//JOptionPane.showMessageDialog(null, "Server started", "alert", JOptionPane.INFORMATION_MESSAGE);

	String cmd = "";
	BufferedReader in = null;
	try
	{
		in = new BufferedReader(new InputStreamReader(System.in));
		while(!(cmd = in.readLine()).equals("quit"))
		{
			if(cmd.equals("start"))
			{
				System.out.print("Port: ");
				MessageServer server = new MessageServer(Integer.parseInt(in.readLine()));
				new Thread(server).start();
				servers.addElement(server);
				//JOptionPane.showMessageDialog(null, "Server started", "alert", JOptionPane.INFORMATION_MESSAGE);
			}

		}

		for(int i=0;i<servers.size();i++)
		{
			((MessageServer)servers.elementAt(i)).stopServer();
		}
		System.exit(0);
	}catch(Exception e){System.out.println("Error: " + e);}
	 finally
	 {
		 try
		 {
		 in.close();
	 	 }catch(Exception e){System.out.println("Error: " + e);}
	 }


}

}