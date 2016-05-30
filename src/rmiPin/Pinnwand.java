package rmiPin;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Permission;

import javax.security.auth.AuthPermission;
import javax.security.auth.login.AccountException;

public interface Pinnwand extends Remote {	
	public int login (String password) throws RemoteException, AccountException;
	public int getMessageCount() throws RemoteException, AccountException;
	public String[] getMessages() throws RemoteException, AccountException;
	public String getMessage(int index) throws RemoteException, AccountException;
	public boolean putMessage(String msg) throws RemoteException, AccountException;
}