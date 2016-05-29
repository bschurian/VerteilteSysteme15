package rmiPin;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PinnwandImpl extends UnicastRemoteObject implements Pinnwand {

	public static String serviceName = "Pinnwand";

	private static final long serialVersionUID = 1L;

	final List<Message> messages;
	final int maxNumMessages;
	/*
	 * life time of a message in seconds
	 */
	final long messageLifetime;
	final int maxLengthMessage;

	final static String password = "1234";
	final Collection<String> users;// TODO richtig

	final Thread messageCuller;

	public PinnwandImpl() throws RemoteException {
		this(20, 10* 60, 160, "Pinnwand");
	}

	public PinnwandImpl(int maxNumMessages, long messageLifetime,
			int maxLengthMessage, String serviceName) throws RemoteException {
		super();
		this.messages = new ArrayList<Message>();

		this.maxNumMessages = maxNumMessages;
		this.messageLifetime = messageLifetime;
		this.maxLengthMessage = maxLengthMessage;
		// this.serviceName = serviceName;
		this.users = new ArrayList<String>();// TODO richtig

		this.messageCuller = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					int maxIndex = messages.size() - 1;
					for (int i = maxIndex; i >= 0; i--) {
						Message message = messages.get(i);
						if (System.nanoTime() > message.getCreationTime()
								+ messageLifetime * 1000000 * 1000
								) {
							messages.remove(i);
						}
					}
					try {
						synchronized (this) {//this bezieht sich hier auf das runnable
							this.wait(1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
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
		final String[] strings = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++)
			strings[i] = messages.get(i).getMessage();
		return strings;
	}

	@Override
	public String getMessage(int index) throws RemoteException {
		Message msg = messages.get(index);
		return msg.getMessage();
	}

	@Override
	public boolean putMessage(String msg) throws RemoteException {
		boolean success = false;
		if (msg.length() > maxLengthMessage)
			throw new IllegalArgumentException("Message goes over character limit");		
		if (messages.size() >= maxNumMessages)
			success = messages.add(new Message(msg));
		return success;
	}

	private class Message {
		private long creationTime;
		private String message;

		Message(String message) {
			creationTime = System.nanoTime();
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

		Pinnwand pinnwand;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			// if (args.length > 0)
			// registry = LocateRegistry.getRegistry(args[0]);
			// else
			// registry = LocateRegistry.getRegistry("localhost");

			registry = LocateRegistry.createRegistry(1099);

			pinnwand = new PinnwandImpl();

			registry.bind(serviceName, pinnwand);

			((PinnwandImpl) pinnwand).messageCuller.start();

			System.out.println("Pinnwand Server started. Ready ...");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}
