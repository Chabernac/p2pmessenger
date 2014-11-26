package chabernac.utils;


import org.junit.Test;

import java.net.SocketException;
import java.util.List;

import chabernac.tools.SimpleNetworkInterface;


public class NetToolsTest {
    @Test
    public void testGetLocalExposedInterfaces() throws SocketException{
        List<SimpleNetworkInterface> theInterfaces = NetTools.getLocalExposedInterfaces();
        for(SimpleNetworkInterface theInterface : theInterfaces){
            System.out.println(theInterface);
        }
        
    }

}
