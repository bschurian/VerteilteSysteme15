package mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MailServer{

	public static final int PORTNUMBER = 8090;
    public static final int MAX_USERS = 5;

    public Collection<Socket> unregisteredClientSockets;
    public Collection<Session> clients;
    
    protected ServerSocket serverSocket;
    
    public MailServer(final int port){
    	try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			this.serverSocket = null;
			e.printStackTrace();
		}
        unregisteredClientSockets = new ArrayList<>();
        clients = new ArrayList<>();
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
//            final Collection<Socket> unregisteredClientSockets = new ArrayList<Socket>();
//            final Collection<Session> sessions = new ArrayList<MailServer.Session>();

            // setting up a connection
            connectionBuild: while (true) {
            	final Socket client =  mailServer.serverSocket.accept();            	
            	mailServer.unregisteredClientSockets.add(client);
                
                mailServer.out.println(new Response(204,0,new String[]{"Connected to sever"}).json());

                String userInput;
                while ((userInput = mailServer.in.readLine()) != null) {
                    try {
                        Gson gson = new Gson();
                        Request request = gson.fromJson(userInput, Request.class);
                        mailServer.out.println(mailServer.handleRequest(request));

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("server is kill");
    }

    public Response handleRequest(Request request){
        String command = request.getCommand();
        final StatusAndData statusAndData;
        switch(command){
            case "login":
            	statusAndData = login(request.getParams()[0]);
            	break;
            default:
            	statusAndData = new StatusAndData(StatusCodes.SERVICEUNAVAILABLE, null);
            	break;
        }
        return new Response(statusAndData.getStatus(), request.getSequence(), statusAndData.getData());
    }

    private StatusAndData login(String username){
    	String welcome = "Welcome to the server";
    	
    	return new StatusAndData(1, new String[]{welcome});
    }

    private class Session{
        private String username;
        private Socket client;

        private PrintWriter out;
        private BufferedReader in;
        
        Session(final String username, final Socket client){
        	super();
        	this.username = username;
        	this.client = client;
        	try {
				this.out = new PrintWriter(this.client.getOutputStream());
				this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    private class StatusAndData{
    	private final int status;
    	private final String[] data;
    	StatusAndData(int status, String[] data) {
    		super();
    		this.status = status;
    		this.data = data;
    	}
    	public int getStatus(){
    		return this.status;
    	}
    	public String[] getData(){
    		return this.data;
    	}
    }
}
