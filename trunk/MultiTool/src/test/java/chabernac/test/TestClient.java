package chabernac.test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;

public class TestClient {
  private MessengerClientService myMessengerClientService = null;
  
  public TestClient(String aFirstName, String aLastName, int aPort){
    
    try{
      MessengerUser theUser = new MessengerUser();
      theUser.setFirstName(aFirstName);
      theUser.setLastName(aLastName);
      //theUser.setUserName(System.getProperty("user.name"));
      theUser.setUserName(aFirstName + "_" + aLastName);
      theUser.setHost(InetAddress.getLocalHost().getHostAddress());
      theUser.setStatus(MessengerUser.ONLINE);
      theUser.setRmiPort(aPort);
      
      
      myMessengerClientService = new MessengerClientService(theUser, "x20d1148", 2099);
      //myMessengerClientService.addObserver(new ClientServiceObserver());
      myMessengerClientService.register();
      Thread.sleep(10000);
      myMessengerClientService.unregister();
      System.exit(0);
      }catch(Exception e){
        e.printStackTrace();
      }
  }
  
  private class ClientServiceObserver implements Observer{

    public void update(Observable o, Object arg) {
      System.out.println("----------Current users-----------");
      for(Iterator i=myMessengerClientService.getUsers().values().iterator();i.hasNext();){
        System.out.println(i.next());
      }
      System.out.println("----------End Currentusers--------");
    }
    
  }

  public static void main(String[] args) {
    new TestClient(args[0], args[1], Integer.parseInt(args[2]));
  }

}
