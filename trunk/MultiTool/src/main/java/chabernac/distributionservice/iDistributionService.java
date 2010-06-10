package chabernac.distributionservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chabernac.command.Command;

public interface iDistributionService extends Remote {
  public Command getDistributionCommand() throws RemoteException;

}
