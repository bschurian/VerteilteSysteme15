package rmiPin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PinnwandIpml implements Pinnwand {
	
	final List<Message> messages;
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
		this.messages = new ArrayList<Message>();
		
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
		cullExpiredMessages();
		return messages.size();
	}

	@Override
	public String[] getMessages() throws RemoteException {
		cullExpiredMessages();
		return (String[]) messages.toArray();
	}

	@Override
	public String getMessage(int index) throws RemoteException {
		cullExpiredMessages();
		return messages.get(index).getMessage();
	}

	@Override
	public boolean putMessage(String msg) throws RemoteException {
		final boolean success;
		cullExpiredMessages();
		if(messages.size() >= maxNumMessages) throw new IndexOutOfBoundsException();
		if(msg.length() > maxLengthMessage) throw new IllegalArgumentException();
		success = messages.add(new Message(msg));
		return success;
	}

	private void cullExpiredMessages(){
		long current = System.currentTimeMillis();
		messages.forEach((message) -> {
			if(message.getCreationTime() + messageLifetime > current){
				messages.remove(message);
			}
		});
	}

	private class Message{

		private long creationTime;
		private String message;

		Message(String message){
			creationTime = System.currentTimeMillis();
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public long getCreationTime() {
			return creationTime;
		}
	}

}


