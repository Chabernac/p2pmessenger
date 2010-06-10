/*
 * Created on 13-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketDecorator {
	private Socket mySocket = null;
	private ObjectInputStream myObjectInputStream = null;
	private ObjectOutputStream myObjectOutputStream = null;
	
	public SocketDecorator(InetAddress address, int port) throws IOException{
		mySocket = new Socket(address, port);
//    mySocket.setSoTimeout(10000);
	}
	
	public SocketDecorator(String host, int port) throws IOException{
		mySocket = new Socket(host, port);
//    mySocket.setSoTimeout(10000);
	}
	
	public SocketDecorator(Socket aSocket) throws SocketException{
		mySocket = aSocket;
//    mySocket.setSoTimeout(10000);
	}
	
	public Socket getSocket(){
		return mySocket;
	}
	
	public ObjectInputStream getObjectInputStream() throws IOException{
		if(myObjectInputStream == null) myObjectInputStream = new ObjectInputStream(mySocket.getInputStream());
		return myObjectInputStream;
	}
	
	public ObjectOutputStream getObjectOuputStream() throws IOException{
		if(myObjectOutputStream == null) myObjectOutputStream = new ObjectOutputStream(mySocket.getOutputStream());
		return myObjectOutputStream;
	}
	
	public void close() throws IOException{
    if(myObjectOutputStream != null){
      myObjectOutputStream.flush();
      myObjectOutputStream.close();
      myObjectOutputStream = null;
    }
		mySocket.close();
	}
	
	public InputStream getInputStream() throws IOException{
		return mySocket.getInputStream();
	}
	
	public OutputStream getOutputStream() throws IOException{
		return mySocket.getOutputStream();
	}


}
