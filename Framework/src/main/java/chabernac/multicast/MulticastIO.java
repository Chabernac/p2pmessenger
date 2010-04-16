package chabernac.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import chabernac.log.Logger;


/**
 * @author D1DAB1L
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MulticastIO implements Runnable{
    private int myPort;
    private int myPacketLength;
    private InetAddress myInetAddress = null;
    private MulticastSocket mySocket = null;
    private DatagramProtocol myDatagramProtocol = null;
    private boolean stop = false;
    
    public MulticastIO(int aPort, InetAddress anAddress, int aPacketLength, DatagramProtocol aProtocol){
        myPort = aPort;
        myInetAddress = anAddress;
        myPacketLength = aPacketLength;
        myDatagramProtocol = aProtocol;
        startIO();
    }
    
    public void startIO(){
        stop = false;
        try{
	        mySocket = new MulticastSocket(myPort);
	        mySocket.joinGroup(myInetAddress);
	        mySocket.setTimeToLive(5);
	        Logger.log(this, "Multicast socket started on: " + myInetAddress.toString() + ":" + myPort + " ttl:" + mySocket.getTimeToLive());
	        new Thread(this).start();
        }catch(IOException e){
            Logger.log(this, "IOException occured", e);
        }
    }
    
    public void stopIO(){
        stop = true;
        try {
            mySocket.leaveGroup(myInetAddress);
        } catch (IOException e) {
            Logger.log(this,"Could not leave group", e);
        }
        mySocket.close();
    }
    
    public void run(){
        try{
	        while(!stop){
	            DatagramPacket thePacket = new DatagramPacket(new byte[myPacketLength], myPacketLength);
	            mySocket.receive(thePacket);
	            new Thread(new DatagramHandler(thePacket)).start();
	        }
        }catch(IOException e){
            Logger.log(this, "IOException occured", e);
        }
    }
    
    public void sendDatagramPacket(byte[] theContent){
        DatagramPacket thePacket = new DatagramPacket(theContent, theContent.length);
        thePacket.setAddress(myInetAddress);
        thePacket.setPort(myPort);
        Logger.log(this,"Sending datagram packet: " + new String(thePacket.getData()));
        try {
            mySocket.send(thePacket);
        } catch (IOException e) {
            Logger.log(this, "IOException occured", e);
        }
    }
    
    private class DatagramHandler implements Runnable{
        private DatagramPacket myPacket = null;
        
        public DatagramHandler(DatagramPacket aPacket){
            myPacket = aPacket;
        }
        
        public void run(){
            myDatagramProtocol.handle(myPacket.getData(), MulticastIO.this);
        }
    }
    
    

}
