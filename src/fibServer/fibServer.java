package fibServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class fibServer {

	public static void main(String[] args) {
		try {
			final ServerSocket server = new ServerSocket(1432);
			final Socket clientSocket = server.accept();

            while(!clientSocket.isClosed()) {
                final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                String userInput;
                if ((userInput = in.readLine()) != null) {
                    try {
                        final int number = Integer.parseInt(userInput);
                        final int result = fibonacci(number);
                        out.println(number + ". number in Fibonacci's sequence: " + result);
                    } catch (NumberFormatException e) {
                        out.println(userInput + " is not accepted");
                        clientSocket.close();
                    }
                }
            }

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int fibonacci(final int n) {
        if(n < 1) return 0;
        if (n <= 2)return 1;
		return fibonacci(n - 1) + fibonacci(n - 2);
	}
}
