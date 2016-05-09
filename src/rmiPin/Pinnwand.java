package rmiPin;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Pinnwand extends Remote {
	 public int login (String password) throws RemoteException;
	 public int getMessageCount() throws RemoteException;
	 public String[] getMessages() throws RemoteException;
	 public String getMessage(int index) throws RemoteException;
	 public boolean putMessage(String msg) throws RemoteException;
}