package chabernac.messengerservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface iMessengerService extends Remote{
  public void registerUser(MessengerUser aUser) throws RemoteException, UserInUseException;
  public void updateUser(MessengerUser aUser) throws RemoteException;
  public HashMap getAllUsers() throws RemoteException;
  public void unregisterUser(MessengerUser myUser) throws RemoteException;
}
