package mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class MailClient {

	public static final int PORTNUMBER = 5678;
	public static final int FEHLER_EINGABE = -1;
	public static final int FEHLER_ZAHLENBEREICH = -2;
	public static String help = String
			.format("moegliche Befehle: \nhilfe - Bedienungshilfe wird ausgegeben \n"
					+ "berechne <zahl> - berechnet fibonacci fuer <zahl> \n"
					+ "ende - beendet die Anwendung");

	public static void main(String[] args) {
		// setting up the client
		try {
			int port;
			try {
				port = Integer.parseInt(args[1]);
				if (port < 0)
					throw new NumberFormatException("Port < 0");
			} catch (Exception e) {
				port = PORTNUMBER;
			}
			InetAddress address;
			try {
				address = InetAddress.getByAddress(args[0].getBytes());
			} catch (Exception e) {
				address = InetAddress.getLocalHost();
			}
			final Socket client = new Socket(address, port);
			final PrintWriter out = new PrintWriter(client.getOutputStream(),
					true);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			final BufferedReader consoleInput = new BufferedReader(
					new InputStreamReader(System.in));
			
			System.out.println("Client can now take commands. ");
			
			// reading from console
			String input;
			loop: while ((input = consoleInput.readLine()) != null) {
				switch (input.split(" ")[0]) {
				case "hilfe":
					System.out.println(help);
					break;
				case "ende":
					System.out.println("Closing client");
					break loop;
				case "berechne":
					String maybeNumber;
					try {
						maybeNumber = input.split(" ")[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("command 'berechne' needs a number");
						break;
					}
					// prints to and flushes out
					out.println(maybeNumber);

					String feedback;
					int response = Integer.parseInt(in.readLine());
					switch (response) {
					case FEHLER_EINGABE:
						feedback = "Fehlerhafte Eingabe";
						break;
					case FEHLER_ZAHLENBEREICH:
						feedback = "Ungueltiger Zahlenbereich";
						break;
					default:
						feedback = maybeNumber
								+ ". number in Fibonacci's sequence: "
								+ response;
						break;
					}
					System.out.println(feedback);
					break;
				default:
					System.out.println("command not supported");
					break;
				}

			}

			client.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
