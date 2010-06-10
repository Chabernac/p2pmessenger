package chabernac.messengerservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

class TimeoutFactory extends RMISocketFactory {
	private int timeout;
	public TimeoutFactory(int timeout) {
		this.timeout = timeout;
	}

	public Socket createSocket(String host, int port) throws IOException {
		Socket ret = getDefaultSocketFactory().createSocket(host, port);
		ret.setSoTimeout(timeout);
		return ret;
	}
	public ServerSocket createServerSocket(int port) throws IOException {
		return getDefaultSocketFactory().createServerSocket(port);
	}
}
