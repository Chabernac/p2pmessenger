package chabernac.messengerservice;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface iMessengerControlService {
  public void stopService() throws RemoteException, MalformedURLException, NotBoundException;
  public boolean isAlive();
  public void startService() throws RemoteException, MalformedURLException;
}
