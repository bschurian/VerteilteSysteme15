package rmiPin;

import javax.security.auth.login.AccountException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

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

	final long userTimeout;

	final static String PASSWORD = "1234";
	final Collection<User> users;

	final Thread messageCuller;

	public PinnwandImpl() throws RemoteException {
		this(20, 1 * 60, 160, "Pinnwand");
	}

	public PinnwandImpl(int maxNumMessages, long messageLifetime,
			int maxLengthMessage, String serviceName) throws RemoteException {
		super();
		this.messages = new ArrayList<Message>();

		this.maxNumMessages = maxNumMessages;
		this.messageLifetime = messageLifetime;
		this.maxLengthMessage = maxLengthMessage;
		// this.serviceName = serviceName;
		this.users = new ArrayList<User>();
		this.userTimeout = 1000 * 30;

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
	public int login(String userPassword) throws RemoteException, AccountException {
		try{
			if(userLogged()){
				throw new AccountException("Sie sind schon angemeldet.");
			}
			if(userPassword.equals(PASSWORD)){
				this.users.add(new User(this.getClientHost(), this.userTimeout));
				curlUsers();
			} else {
				throw new IllegalArgumentException("Sie haben ein falsches Passwort eingegeben.");
			}
		} catch(ServerNotActiveException e){
			e.printStackTrace();
			return -1;
		}

		return 1;
	}

	private void curlUsers(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					List<User> toRemove = new ArrayList<>();
					for(User user : users){
						if(!user.isActive()){
							toRemove.add(user);
						}
					}
					users.removeAll(toRemove);
				}
			}
		});
		t.start();
	}


	private boolean userLogged(){
		try {
			String host = this.getClientHost();
			for(User user : users){
				if(user.getHost().equals(host)){
					return true;
				}
			}
			return false;
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
			return false;
		}

	}

	private void checkUserLogged() throws AccountException {
		if(!userLogged()){
			throw new AccountException("Sie muessen sich zuerst ueber das \"login\"-Kommando anmelden");
		}
	}

	private User currentUser() throws ServerNotActiveException {
		String host = this.getClientHost();
		for(User user : users){
			if(user.getHost().equals(host)){
				return user;
			}
		}
		return null;
	}

	private void registerUserActivity() {
		try {
			currentUser().setLastActivity(System.currentTimeMillis());
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getMessageCount() throws RemoteException, AccountException {
		checkUserLogged();
		registerUserActivity();
		return messages.size();
	}

	@Override
	public String[] getMessages() throws RemoteException, AccountException {
		checkUserLogged();
		registerUserActivity();
		final String[] strings = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++)
			strings[i] = messages.get(i).getMessage();
		return strings;
	}

	@Override
	public String getMessage(int index) throws RemoteException, AccountException {
		checkUserLogged();
		registerUserActivity();
		try{
			Message msg = messages.get(index);
			return msg.getMessage();
		} catch(IndexOutOfBoundsException e){
			throw new IllegalArgumentException("Es gibt keine Nachricht an dieser Position.");
		}

	}

	@Override
	public boolean putMessage(String msg) throws RemoteException, AccountException {
		checkUserLogged();
		registerUserActivity();
		boolean success;
		if (msg.length() > maxLengthMessage) {
			throw new IllegalArgumentException("Message goes over character limit");
		}
		if (messages.size() <= maxNumMessages) {
			success = messages.add(new Message(msg));
		} else {
			throw new IllegalArgumentException("-------shit------ " + messages.size() + " --- " + maxNumMessages);
		}
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

	private class User{

		private final String host;
		private long lastActivity;
		private long timeout;

		public User(final String host, long timeout) {
			this.host = host;
			this.lastActivity = System.currentTimeMillis();
			this.timeout = timeout;
		}

		public String getHost(){
			return this.host;
		}

		public void setLastActivity(long activity){
			this.lastActivity = activity;
		}

		public boolean isActive(){
			return System.currentTimeMillis() - timeout < lastActivity;
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
