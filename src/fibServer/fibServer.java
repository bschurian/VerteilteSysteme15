package fibServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

public class fibServer {

	public static void main(String[] args) {
		try {
            int port;
            try{
                port = Integer.parseInt(args[1]);
            } catch(Exception e){
                port = 5678;
            }
			final ServerSocket server = new ServerSocket(port);
			final Socket clientSocket = server.accept();

			final PrintWriter out = new PrintWriter(
					clientSocket.getOutputStream(), true);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			String userInput;
			while ((userInput = in.readLine()) != null) {
				try {
					int result; 
					try{
						final int number = Integer.parseInt(userInput);
						result = fibonacci(number);
					}catch(NumberFormatException nfe){
						result = -1;
					}catch (IllegalArgumentException iaE){
                        result = - 2;
                    }
					out.println(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int fibonacci(final int x) throws IllegalArgumentException{
//		if (x < 0)//Ausnahme 1
//			return ((int) Math.pow(-1, x)) * fibonacci(Math.abs(x));
//		if (x == 0)//Ausnahme 2
//			return 0;
		if(x<1) throw new IllegalArgumentException();
		if (x <= 2)//randbedingung
			return 1;
		return fibonacci(x - 1) + fibonacci(x - 2);
	}
}



