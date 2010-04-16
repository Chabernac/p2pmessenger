package chabernac.server;

import chabernac.log.Logger;

public class EchoProtocol extends AbstractIOProtocol{
  protected void handle(Channel aChannel) {
    aChannel.write("Echo protocol listening...");
    String theLine = null;
    while(!(theLine = aChannel.read()).equalsIgnoreCase("quit")){
      Logger.log(this,"line received: " + theLine);
      aChannel.write(theLine);
    }
    aChannel.write("Bye!");
  }
  
  public static void main(String args[]){
    Logger.setDebug(true);
    Server theServer = new Server(789, new EchoProtocol());
    theServer.startServer();
  }


  

}
