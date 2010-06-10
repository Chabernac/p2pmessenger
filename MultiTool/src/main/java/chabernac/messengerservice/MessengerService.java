package chabernac.messengerservice;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.timer.CommandTimer;




public class MessengerService extends UnicastRemoteObject implements iMessengerService, iMessengerControlService, Command {
  private static String SERVICE = "MessengerService";
  private static Logger logger = Logger.getLogger(MessengerService.class);
  private static final long serialVersionUID = -5771838545182661921L;
  private CommandTimer myTimer = null;
  private int myPort;
  private String myServiceURL = "";

  private HashMap myUsers = null;
  private Map mySyncUsers = null;

  public MessengerService(int aPort) throws RemoteException, UnknownHostException {
    super();
    myPort = aPort;
    init();

  }

  private void init() throws UnknownHostException{
    myUsers = new HashMap();
    mySyncUsers = Collections.synchronizedMap(myUsers);
    myTimer = new CommandTimer(5 * 60 * 1000);
    myServiceURL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":" + myPort + "/" + SERVICE;
  }

  public void startService() throws RemoteException, MalformedURLException{
    try{
      LocateRegistry.createRegistry(myPort);
    }catch(Exception e){
      logger.error("Registry already started at port: " + myPort);
    }
    Naming.rebind(myServiceURL , this);
    myTimer.addCommand(this);
    myTimer.startTimer();
//    logger.debug("MS started");
  }

  public void stopService() throws RemoteException, MalformedURLException, NotBoundException{
    Naming.unbind(myServiceURL);
    myTimer.stop();
    logger.debug("MS stopped");
  }


  public HashMap getAllUsers() {
    return myUsers;
  }


  public void registerUser(MessengerUser aUser) throws RemoteException, UserInUseException {
    if(mySyncUsers.containsKey(aUser.getId())) {
      logger.debug( "User " + aUser.getId() +  " already registered, checking for activeness... ");
      MessengerUser theUser = (MessengerUser)mySyncUsers.get(aUser.getId());
      try{
        iMessengerClientService theMessengerClientService = (iMessengerClientService)Naming.lookup("rmi://"  + theUser.getHost() + ":" + theUser.getRmiPort() + "/MessengerClientService");
        theMessengerClientService.getUser();
        logger.debug("User " + aUser.getId() + " already registered and running, throwing exception");
        throw new UserInUseException("This user is alread logged on", theUser);
      } catch(Throwable e){
        if(e instanceof UserInUseException) throw (UserInUseException)e;
        logger.debug("User " + theUser.getId()  +  " not respoinding at port: " + theUser.getRmiPort() +  " removing from map");
        mySyncUsers.remove(aUser.getId());
      }
    }
    updateUser(aUser);
  }
  
  public synchronized void updateUser(final MessengerUser aUser) {
    logger.debug( "Updating user: " + aUser.getId());
//  System.out.println("Updating client: " + aUser.getUserName());
    mySyncUsers.put(aUser.getId(), aUser);
    for(Iterator i=mySyncUsers.values().iterator();i.hasNext();){
      final MessengerUser theUser = (MessengerUser)i.next();
      new Thread(new Runnable(){
        public void run(){
          try{
            iMessengerClientService theMessengerClientService = (iMessengerClientService)Naming.lookup("rmi://"  + theUser.getHost() + ":" + theUser.getRmiPort() + "/MessengerClientService");
//          System.out.println("Invoking update client " + aUser + " on: " + theUser);
            theMessengerClientService.userChanged(aUser);
          }catch(Exception e){
//            logger.error("An error occured while invoking userchange on client: " + theUser.getHost() + ": " + theUser.getRmiPort(), e);
          }
        }
      }).start();

    }
  }

  public void execute() {
    logger.debug( "checking for client activity" );
    for(Iterator i=mySyncUsers.values().iterator();i.hasNext();){
      MessengerUser theUser = (MessengerUser)i.next();
      try{
        iMessengerClientService theMessengerClientService = (iMessengerClientService)Naming.lookup("rmi://"  + theUser.getHost() + ":" + theUser.getRmiPort() + "/MessengerClientService");
        MessengerUser theCurrentUser = theMessengerClientService.getUser();
        if(!theCurrentUser.equals(theUser)){
          updateUser(theCurrentUser);
        }
      }catch(Exception e){
//        logger.error("An error occured while invoking getuser on client: " + theUser.getHost(), e);
        try {
          unregisterUser(theUser);
        } catch (RemoteException e1) {
//          logger.error("An error occured while invoking unregister user", e1);
        }
      }
    }
  }

  public synchronized void unregisterUser(final MessengerUser aUser) throws RemoteException {
//  System.out.println("Remove user: " + aUser + " received");
    logger.debug( "Unregistering user: " + aUser.getId());
    mySyncUsers.remove(aUser.getId());
    for(Iterator i=mySyncUsers.values().iterator();i.hasNext();){
      final MessengerUser theUser = (MessengerUser)i.next();
      new Thread(new Runnable(){
        public void run(){
          try{
            iMessengerClientService theMessengerClientService = (iMessengerClientService)Naming.lookup("rmi://"  + theUser.getHost() + ":" + theUser.getRmiPort() + "/MessengerClientService");
//          System.out.println("Invoking remove user on client: " + theUser);
            theMessengerClientService.removeUser(aUser);
          }catch(Exception e){
//            logger.error("An error occured while invoking removeUser on client: " + theUser.getHost(), e);
          }
        }
      }).start();

    }
  }

  public static void main(String args[]){
    try{
      BasicConfigurator.configure();
      if(args.length > 1){
        int theRMITimeout = Integer.parseInt( args[1] );
        RMISocketFactory.setSocketFactory(new TimeoutFactory(theRMITimeout));
      }


      MessengerService theService = new MessengerService(Integer.parseInt(args[0]));
      //System.out.println("Starting server at port: " + args[0]);
      theService.startService();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public boolean isAlive() {
    return true;
  }
}
