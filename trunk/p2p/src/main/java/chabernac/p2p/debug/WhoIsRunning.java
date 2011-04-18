package chabernac.p2p.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import chabernac.protocol.DynamicSizeExecutor;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.iWhoIsRunningListener;
import chabernac.protocol.routing.RoutingProtocol.Command;

public class WhoIsRunning implements Runnable{
  private final iWhoIsRunningListener myListener;
  private final String myHost;
  private final int myPortFrom;
  private final int myPortTo;
  private final ExecutorService myService = DynamicSizeExecutor.getMediumInstance();

  public WhoIsRunning(iWhoIsRunningListener anListener, String aHost, int aPortFrom, int aPortTo) {
    super();
    myListener = anListener;
    myPortFrom = aPortFrom;
    myPortTo = aPortTo;
    myHost = aHost;
  }
  
  public void run(){
    for(int port=myPortFrom;port<=myPortTo;port++){
      myService.execute(new Scanner(myHost, port));
    }
  }

  private class Scanner implements Runnable{
    private final String myHost;
    private final int myPort;

    public Scanner(String anHost, int anPort) {
      super();
      myHost = anHost;
      myPort = anPort;
    }

    public void run(){
      Socket theSocket = null;
      try {
        theSocket = new Socket(myHost, myPort);
        PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
        BufferedReader theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
        String theMessage = RoutingProtocol.ID + RoutingProtocol.Command.WHO_ARE_YOU.name();
        theWriter.println(theMessage);
        theWriter.flush();
        String theReturnMessage = theReader.readLine();
        myListener.peerDetected(myHost, myPort, theReturnMessage);
      }catch(Exception e){
        myListener.noPeerAt(myHost, myPort);
      } finally {
        if(theSocket != null){
          try {
            theSocket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

}
