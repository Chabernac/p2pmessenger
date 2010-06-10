package chabernac.distributionservice;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import chabernac.command.Command;

public class DistributionService extends UnicastRemoteObject implements iDistributionService{
  //private static Logger logger = Logger.getLogger(DistributionService.class);
  
  private File myDistributionList = null;
  private String myDistributionJarFile = null;
  private String myHost = "";
  private int myPort;
  private String myServiceURL = "";
  
  private DistributionService(String aHost, int aPort) throws RemoteException, MalformedURLException{
    super();
    myHost = aHost;
    myPort = aPort;
  }
  
  public DistributionService(File aDistributionList, String aHost, int aPort) throws RemoteException, MalformedURLException{
    this(aHost, aPort);
    myDistributionList = aDistributionList;
  }
  
  public DistributionService(String aDistributionJarFile, String aHost, int aPort) throws RemoteException, MalformedURLException{
    this(aHost, aPort);
    myDistributionJarFile = aDistributionJarFile;
  }

  
  public void register() throws RemoteException, MalformedURLException{
    myServiceURL = "rmi://"  + myHost + ":" + myPort + "/DistributionService";
    Naming.rebind(myServiceURL, this);
    //logger.debug("Distribution service started at: " + myServiceURL);
  }
  
  public void unregister() throws RemoteException, MalformedURLException, NotBoundException{
    Naming.unbind(myServiceURL);
  }

  public Command getDistributionCommand() throws RemoteException {
    if(myDistributionJarFile != null) return new DistributionCommand(myDistributionJarFile);
    return new DistributionCommand(myDistributionList);
  }

}
