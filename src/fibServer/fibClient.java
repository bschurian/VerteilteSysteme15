package fibServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class fibClient {
	public static void main(String[] args) {
		System.out.println("Enter a Numer to fibonaccionize: ");
		final Scanner scanner = new Scanner(System.in);
		final int x = scanner.nextInt();
		try {
			final Socket client = new Socket(InetAddress.getLocalHost(), 1432);
			final InputStream in = client.getInputStream();
			final Scanner scanner2 = new Scanner(in);
			
			final OutputStream out = client.getOutputStream();

			final PrintStream outWriter = new PrintStream(out);
			outWriter.println(x+" ");

			final int result = scanner2.nextInt();
			System.out.printf("Fibonacci number of %s is %s \n", x, result);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			scanner.close();
		}
	}
}
