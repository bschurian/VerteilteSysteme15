package rmiPin;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PinnwandImpl extends UnicastRemoteObject implements Pinnwand {
	
	public static String serviceName = "pinnwandImpl-server";
	
	final List<Message> messages;
	final int maxNumMessages;
	/*
	 * life time of a message in seconds
	 */
	final long messageLifetime;
	final int maxLengthMessage;
	
	final static String password = "1234";
	final Collection<String> users;//TODO richtig
	
	public PinnwandImpl() throws RemoteException{
		this(20, 10, 160, "Pinnwand");
	}
	
	public PinnwandImpl(int maxNumMessages,
			long messageLifetime, int maxLengthMessage, String serviceName) throws RemoteException {
		super();
		this.messages = new ArrayList<Message>();
		
		this.maxNumMessages = maxNumMessages;
		this.messageLifetime = messageLifetime;
		this.maxLengthMessage = maxLengthMessage;
		this.serviceName = serviceName;
		this.users = new ArrayList<String>();
	}

	@Override
	public int login(String password) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMessageCount() throws RemoteException {
//		cullExpiredMessages();
		return messages.size();
	}

	@Override
	public String[] getMessages() throws RemoteException {
//		cullExpiredMessages();
		return (String[]) messages.toArray();
	}

	@Override
	public String getMessage(int index) throws RemoteException {
//		cullExpiredMessages();
		Message msg = messages.get(index);
		return msg.getMessage();
	}

	@Override
	public boolean putMessage(String msg) throws RemoteException {
		final boolean success;
//		cullExpiredMessages();
		if(messages.size() >= maxNumMessages) throw new IndexOutOfBoundsException();
		if(msg.length() > maxLengthMessage) throw new IllegalArgumentException();
		success = messages.add(new Message(msg));
		return success;
	}

	private void cullExpiredMessages(){
		long current = System.currentTimeMillis();
		ArrayList<Message> oldMessages = new ArrayList<>();
		messages.forEach((message) -> {
			if(message.getCreationTime() + messageLifetime > current){
				oldMessages.add(message);
			}
		});
		messages.removeAll(oldMessages);
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
	public static void main(String args[]) {		 

		Registry registry;
		
		Pinnwand timeServer;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			if (args.length  > 0)
				registry = LocateRegistry.getRegistry(args[0]);
			else 
				registry = LocateRegistry.getRegistry("localhost");

			timeServer = new PinnwandImpl();
			
			registry.bind(serviceName, timeServer);

			System.out.println("Time Server started. Ready ...");
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}


