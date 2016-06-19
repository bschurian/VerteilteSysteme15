package mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MailServer {
	
	private MailServer mailServer;
	
	public static final int PORTNUMBER = 8090;
	public static final int MAX_USERS = 5;

	public Collection<Socket> unregisteredClientSockets;
	public Collection<RegisteredSession> clients;

	protected ServerSocket serverSocket;

	public MailServer(final int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			this.serverSocket = null;
			e.printStackTrace();
		}
		unregisteredClientSockets = new ArrayList<>();
		clients = new ArrayList<>();
		this.mailServer = this;
	}

	public static void main(String[] args) {
		// setting up the server
		try {
			int port;
			try {
				port = Integer.parseInt(args[0]);
				if (port <= 0)
					throw new NumberFormatException("Port number < 0");
			} catch (Exception e) {
				port = PORTNUMBER;
			}

			final MailServer mailServer = new MailServer(port);
			// final Collection<Socket> unregisteredClientSockets = new
			// ArrayList<Socket>();
			final Collection<RegisteredSession> sessions = new ArrayList<RegisteredSession>();

			// setting up a connection
			connectionBuild: while (true) {
				final Socket client = mailServer.serverSocket.accept();

				if (!(sessions.size() <= MAX_USERS)) {
					final PrintWriter out = new PrintWriter(
							client.getOutputStream(), true);
					out.println(new Response(StatusCodes.TOOMANYREQUESTS, 0,
							new String[] { "Connected to sever" }).json());
					out.close();
					client.close();
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("server is kill");
	}

	public Response handleRequest(Request request) {
		String command = request.getCommand();
		final StatusAndData statusAndData;
		switch (command) {
			case "login":
				statusAndData = login(request.getParams()[0]);
				break;
			case "logout":
				statusAndData = login(request.getParams()[0]);
				break;
			case "who":
				statusAndData = login(request.getParams()[0]);
				break;
			case "time":
				statusAndData = login(request.getParams()[0]);
				break;
			case "ls":
				statusAndData = login(request.getParams()[0]);
				break;
			case "chat":
				statusAndData = login(request.getParams()[0]);
				break;
			case "notify":
				statusAndData = login(request.getParams()[0]);
				break;
			case "note":
				statusAndData = login(request.getParams()[0]);
				break;
			case "notes":
				statusAndData = login(request.getParams()[0]);
				break;
			default:
				statusAndData = new StatusAndData(StatusCodes.SERVICEUNAVAILABLE,
						null);
				break;
		}
		return new Response(statusAndData.getStatus(), request.getSequence(),
				statusAndData.getData());
	}

	private StatusAndData login(String username) {
		String welcome = "Welcome to the server";

		return new StatusAndData(1, new String[] { welcome });
	}

	private class RegisteredSession {		
		private final String username;
		
		private final Socket client;
		private final PrintWriter out;
		private final BufferedReader in;
		
		private boolean loggedIn = false;
		
		public RegisteredSession(final MailServer mailServer,
				final String username, final Socket client) throws IOException {
			this.username = username;
			this.client = client;
			this.out = new PrintWriter(this.client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(
					this.client.getInputStream()));
		}

		public Thread start() {
			return new Thread(new Runnable() {

				@Override
				public void run() {
					String userInput;

					try {
						while ((userInput = in.readLine()) != null) {
							try {
								Gson gson = new Gson();
								Request request = gson.fromJson(userInput,
										Request.class);
								out.println(mailServer.handleRequest(request));
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							this.finalize();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		
		public void setAsLoggedIn(){
			this.loggedIn = true;
		}
	}

	private class StatusAndData {
		private final int status;
		private final String[] data;

		public StatusAndData(int status, String[] data) {
			super();
			this.status = status;
			this.data = data;
		}

		public int getStatus() {
			return this.status;
		}

		public String[] getData() {
			return this.data;
		}
	}
	
	private interface Befehl {
		public StatusAndData anwenden(final String[] params);
	}
	
	private class BefehlFactory{
		private final Map<String, Befehl> befehle;
		private BefehlFactory() {
			this.befehle = new HashMap<String, MailServer.Befehl>();
		}
		public void addBefehl(final String name, final Befehl befehl) {
			this.befehle.put(name, befehl);
		}
		public void executeBefehl(final String name, String[] params) {
			if(this.befehle.containsKey(name)){
				this.befehle.get(name).anwenden(params);
			}
		}
		public BefehlFactory init() {
			final BefehlFactory befehlFactory = new BefehlFactory();
			befehlFactory.addBefehl("login", (params) -> mailServer.login(params[0]));
			return befehlFactory;
		}
		
	}
}
