package rmiPin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class PinnwandIpml implements Pinnwand {
	
	final List<String> messages;
	final String PASSWORD = "password1234";
	final int maxNumMessages;
	/*
	 * life time of a message in seconds
	 */
	final long messageLifetime;
	final int maxLengthMessage;
	final String nameOfService;
	
	
	public PinnwandIpml(int maxNumMessages,
			long messageLifetime, int maxLengthMessage, String nameOfService) {
		super();
		this.messages = new ArrayList<String>();
		
		this.maxNumMessages = maxNumMessages;
		this.messageLifetime = messageLifetime;
		this.maxLengthMessage = maxLengthMessage;
		this.nameOfService = nameOfService;
	}

	@Override
	public int login(String password) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMessageCount() throws RemoteException {
		return messages.size();
	}

	@Override
	public String[] getMessages() throws RemoteException {
		return (String[]) messages.toArray();
	}

	@Override
	public String getMessage(int index) throws RemoteException {
		return messages.get(index);
	}

	@Override
	public boolean putMessage(String msg) throws RemoteException {
		final boolean success;
		if(messages.size() >= maxNumMessages) throw new IndexOutOfBoundsException();
		if(msg.length() > maxLengthMessage) throw new IllegalArgumentException();
		success = messages.add(msg);
		return success;
	}
}
