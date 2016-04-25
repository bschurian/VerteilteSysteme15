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
                    out.println(input);
                    String line = in.readLine();
                    if(line == null){
                        System.out.println("Connection already closed");
                        break;
                    }
                    System.out.println(line);
                    System.out.println(instruction);
                }  catch(IOException e){
                    System.err.println(e.getMessage());
                }
            }

		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}
