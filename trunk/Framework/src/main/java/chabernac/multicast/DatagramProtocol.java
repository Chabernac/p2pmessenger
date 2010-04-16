/*
 * Created on Feb 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package chabernac.multicast;


/**
 * @author D1DAB1L
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DatagramProtocol {
    public void handle(byte[] theContent, MulticastIO anIO);
}
