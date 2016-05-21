package fibServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

public class fibServer {

	public static final int PORTNUMBER = 5678;
	public static final int FEHLER_ZAHLENBEREICH = -2;
	public static final int FEHLER_EINGABE = -1;

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
			@SuppressWarnings("resource")
			final ServerSocket server = new ServerSocket(port);
			Socket clientSocket = null;
			
			boolean killClientFlag;

			// setting up a connection
			connectionBuild: while (true) {				
				clientSocket = server.accept();
				final PrintWriter out = new PrintWriter(
						clientSocket.getOutputStream(), true);
				final BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				
				killClientFlag = false;

				String userInput;
				// calculation and sending of fib(n)
				while ((userInput = in.readLine()) != null) {
					try {
						int result;
						try {
							final int number = Integer.parseInt(userInput);
							result = fibonacci(number);
						} catch (NumberFormatException nfe) {
							result = FEHLER_EINGABE;
							killClientFlag = true;//uncomment so server is very unforgiving
						} catch (IllegalArgumentException iaE) {
							result = FEHLER_ZAHLENBEREICH;
							killClientFlag = true;//uncomment so server is unforgiving
						}
						// prints to and flushes out
						out.println(result);
						//killing(closing) of client happens here if he has incurred our wrath
						if(killClientFlag){
							clientSocket.close();
							continue connectionBuild;						
						}
					} catch (Exception e) {
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

	public static int fibonacci(final int x) throws IllegalArgumentException {
		// if (x < 0)//Ausnahme 1
		// return ((int) Math.pow(-1, x)) * fibonacci(Math.abs(x));
		// if (x == 0)//Ausnahme 2
		// return 0;
		if (x < 1)
			throw new IllegalArgumentException();
		if (x <= 2)// randbedingung
			return 1;
		return fibonacci(x - 1) + fibonacci(x - 2);
	}
}
