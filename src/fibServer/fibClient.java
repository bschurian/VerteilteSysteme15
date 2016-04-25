package fibServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fibClient {
	public static void main(String[] args) {
		final String instruction = "--- Enter a number to fibonaccionize: ";
        System.out.println(instruction);
        try {
			final Socket client ;
			InetAddress address = InetAddress.getByAddress(args[0].getBytes());
			int port = -1;
			port = Integer.parseInt(args[1]);
			if(address != null)address = InetAddress.getLocalHost();
			if(port==-1)port = 5678;
			client = new Socket(address, 5678);
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
                } catch(IOException e){
                    System.err.println("Please enter a valid number: ");
                }
            }

		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}