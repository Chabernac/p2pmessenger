package chabernac.server;

import org.apache.log4j.Logger;

public class EchoProtocol extends AbstractIOProtocol{
  private static Logger logger = Logger.getLogger(EchoProtocol.class);
  
  protected void handle(Channel aChannel) {
    //aChannel.write("Echo protocol listening...");
    String theLine = null;
    while(!(theLine = aChannel.read()).equalsIgnoreCase("quit")){
      try{
	      logger.debug("line received: " + theLine);
	      try{
	    	  Thread.sleep(500);
	      }catch(Exception e){}
	      aChannel.write(theLine);
      }catch(Exception e){
    	  e.printStackTrace();
      }
    }
    aChannel.write("Bye!");
  }
  
  public static void main(String args[]){
    Server theServer = new Server(789, new EchoProtocol());
    theServer.startServer();
    
    Server theServer2 = new Server(790, new EchoProtocol());
    theServer2.startServer();
    
    try{
    	Thread.sleep(1000);
    	//theServer2.send("localhost", 789, "hallo\r\n".getBytes());
    	//theServer2.send("localhost", 789, "quit\r\n".getBytes());
    	Thread.sleep(1000);
    	//theServer2.send("localhost", 789, "hallo2\r\n".getBytes());
    	Thread.sleep(1000);
    	//theServer.send("localhost", 790, "toedelo\r\n".getBytes());
    	//theServer2.send("localhost", 789, "toedeloe".getBytes());
    }catch(Exception e){
    	e.printStackTrace();
    }
  }


  

}
