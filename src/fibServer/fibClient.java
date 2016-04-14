package fibServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class fibClient {
	public static void main(String[] args) {
		System.out.println("Enter a Numer to fibonaccionize: ");
		final Scanner scanner = new Scanner(System.in);
		final int x = scanner.nextInt();
		try {
			Socket client = new Socket(InetAddress.getLocalHost(), 10);
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();

			new BufferedWriter(new PrintWriter(out)).write(x);;

			final Scanner scanner2 = new Scanner(new BufferedReader(new InputStreamReader(in)));
			final int result = scanner2.nextInt();
			System.out.printf("Fibonacci number of %s is %s \n", x, result);
			scanner2.close();

			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			scanner.close();
		}
	}
}
