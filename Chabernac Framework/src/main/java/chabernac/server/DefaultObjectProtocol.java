package chabernac.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public abstract class DefaultObjectProtocol implements Protocol
{
	protected ObjectInputStream objectIn = null;
	protected ObjectOutputStream objectOut = null;

	public void handle(InputStream inputStream, OutputStream outputStream) throws Exception
	{
			  objectOut = new ObjectOutputStream(outputStream);
			  objectIn = new ObjectInputStream(inputStream);
			  handle();
	}

	public void send(Object aObject) throws Exception
	{
		objectOut.writeObject(aObject);
	}

	public Object receive() throws Exception
	{
		return objectIn.readObject();
	}

	public abstract void handle();
}