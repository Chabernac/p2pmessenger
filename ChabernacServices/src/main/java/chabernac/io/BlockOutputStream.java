package chabernac.io;

import java.io.IOException;
import java.io.OutputStream;

public class BlockOutputStream extends OutputStream{
	private OutputStream myOutputStream = null;

	public BlockOutputStream(OutputStream anOutputStream){
		myOutputStream = anOutputStream;
	}

	public void write(int anI) throws IOException {
		myOutputStream.write(anI);
	}

	public void write(byte[] aBytes) throws IOException{
		myOutputStream.write(intToByteArray(aBytes.length));
		myOutputStream.write(aBytes);
	}

	private final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	public void close() throws IOException {
		myOutputStream.close();
	}

	public void flush() throws IOException {
		myOutputStream.flush();
	}
	
	

}
