package fibServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fibClient {
	public static void main(String[] args) {
		final String instruction = "--- Enter a number to fibonaccionize: ";
        System.out.println(instruction);
        try {
			final Socket client = new Socket(InetAddress.getLocalHost(), 1432);
            final PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String input;
            while((input = consoleInput.readLine()) != null){
                try {
                    final int number = Integer.parseInt(input);
                    out.println(number);
                    System.out.println(number + ". number in Fibonacci's sequence:\t\t" + in.readLine());
                    System.out.println(instruction);
                } catch(Exception e){
                    System.err.println("Please enter a valid number: ");
                }
            }

		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}