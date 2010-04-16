package chabernac.io;

import java.io.IOException;
import java.io.InputStream;

public class BlockInputStream extends InputStream {
	private InputStream myInputStream = null;

	public BlockInputStream(InputStream anInputStream){
		myInputStream = anInputStream;
	}

	public int read() throws IOException {
		return myInputStream.read();
	}

	public byte[] readBlock() throws IOException{
		byte[] theBytes = new byte[4];
		myInputStream.read(theBytes);
		int theLength = byteArrayToInt(theBytes);
		theBytes = new byte[theLength];
		int theReadBytes = 0;
		while(theReadBytes < theLength){
			theReadBytes += myInputStream.read(theBytes, theReadBytes, theLength - theReadBytes);
		}
		return theBytes;
	}

	private final int byteArrayToInt(byte [] b) {
		return (b[0] << 24)
		+ ((b[1] & 0xFF) << 16)
		+ ((b[2] & 0xFF) << 8)
		+ (b[3] & 0xFF);
	}

	public void close() throws IOException {
		myInputStream.close();
	}
	
	

}
