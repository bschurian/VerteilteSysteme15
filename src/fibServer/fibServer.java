package fibServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class fibServer {
	
	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(10, 0, InetAddress.getLocalHost());
			Socket client = server.accept();

			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();

			final Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(in)));
			final int result = scanner.nextInt();
			scanner.close();

			new BufferedWriter(new PrintWriter(out)).write(fibonacci(result));;
			
			client.close();
			server.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int fibonacci(final int n) {
		if(n<2) return 1;
		return fibonacci(n-1) + fibonacci(n-2);
	}
}
