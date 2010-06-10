package chabernac.test;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.Random;

import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerService;
import chabernac.messengerservice.MessengerUser;
import chabernac.util.Tools;

public class RegisterStressTest {
  private static int counter = 0;
  private static int local_port = 12000;

  /**
   * @param args
   */
  public static void main(String[] args) {
    try{
//      int theServerPort = Tools.findUnusedLocalPort();
    	int theServerPort = 2099;

//      MessengerService theService = new MessengerService(theServerPort);
//      System.out.println("Starting server at port: " + theServerPort);
//      theService.startService();
//      
      int theLocalPort = Tools.findUnusedLocalPort();
      
      
      
      for(int i=0;i<100;i++){
        Register theRegister = new Register(theServerPort);
        new Thread(theRegister).start();
      }
    }catch(Exception e){
      e.printStackTrace();
    }

    // TODO Auto-generated method stub

  }

  private static class Register implements Runnable{
    private int myServerPort;
    private Random myRandom = null;
    
    public Register(int aServerPort){
      myServerPort = aServerPort;
      myRandom = new Random();
    }


    public void run() {
      try{
        counter++;
        MessengerUser theUser = new MessengerUser();
        theUser.setFirstName("First " + counter);
        theUser.setLastName("Last " + counter);
        theUser.setHost(InetAddress.getLocalHost().getHostAddress());
        theUser.setRmiPort(local_port++);
        theUser.setUserName("User" + counter);
        theUser.setVersion("1.0.0");

//        LocateRegistry.createRegistry(theUser.getRmiPort());

        MessengerClientService theService = new MessengerClientService(theUser, "localhost", myServerPort);
        
        Thread.sleep(Math.abs(myRandom.nextLong() % 100));
        
        theService.register();
        
        Thread.sleep(100 + myRandom.nextLong() % 100);
        
        Map theMap = theService.getUsers();
        
        System.out.println("nr of users: " + theMap.size());
        
        Thread.sleep(1000 + myRandom.nextLong() % 100);
        
        theService.unregister();

      }catch(Exception e){
        e.printStackTrace();
      }

    }

  }

}
