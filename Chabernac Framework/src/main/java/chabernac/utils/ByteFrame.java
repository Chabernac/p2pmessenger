package chabernac.utils;

public class ByteFrame{
	  private byte[] frame;

	  public ByteFrame(int frameSize){
		frame = new byte[frameSize];
	  }

	  public void push(byte aByte){
		  for(int i=0;i<frame.length - 1;i++){
			  frame[i] = frame[i + 1];
		  }
		  frame[frame.length - 1] = aByte;
	  }
	  public String toString(){
		  return new String(frame);
	  }

	  public byte[] getBytes(){
		  return frame;
	  }

	  public byte getLastByte(){
		  return frame[frame.length - 1];
	  }
  }