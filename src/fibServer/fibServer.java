package fibServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class fibServer {

	public static void main(String[] args) {
		try {
			final ServerSocket serverSeS = new ServerSocket(1432);
			final Socket server = serverSeS.accept();

			final Scanner scannerIn = new Scanner(server.getInputStream());
			final int x = scannerIn.nextInt();
			
			final int fib= fibonacci(x);
			
			final PrintStream pS = new PrintStream(server.getOutputStream());
			pS.println(fib);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int fibonacci(final int n) {
		if (n <= 2)return 1;
		return fibonacci(n - 1) + fibonacci(n - 2);
	}
}
