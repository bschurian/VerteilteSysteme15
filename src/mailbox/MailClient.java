package mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;


public class MailClient {

	public static final int PORTNUMBER = 5678;
	public static final int FEHLER_EINGABE = -1;
	public static final int FEHLER_ZAHLENBEREICH = -2;
	public static String help = String
			.format("moegliche Befehle: \nhilfe - Bedienungshilfe wird ausgegeben \n"
					+ "login <username> : einloggen des Benutzers mit dem Namen <username> \n"
					+ "logout: Beenden und abmelden \n"
					+ "who: Liste mit verbundenen Benutzern \n"
					+ "time: aktuelle Zeit \n"
					+ "ls <Pfad>: Dateiliste von <Pfad> \n"
					+ "chat <username> <message> : sendet Nachricht an < username > \n"
					+ "notify <message>: benachrichtigt alle eingeloggten Benutzer. \n"
					+ "note <text>: hinterlegt eine Notiz f�r alle Benutzer. \n"
					+ "notes: alle Notizen werden angezeigt.\n"
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
			
			final List<Integer> allSeqs= new ArrayList<Integer>();
			allSeqs.add((int) Math.random()*10000);
			
			// reading from console
			String input;
			loop: while ((input = consoleInput.readLine()) != null) {
				String[] inputWords = input.split(" ");
				switch (inputWords[0]) {
				case "hilfe":
					System.out.println(help);
					break;
				case "ende":
					System.out.println("Closing client");
					break loop;
				default:
					int seq = allSeqs.get(allSeqs.size() - 1);
					out.println(new Request(seq, inputWords[0], Arrays.copyOfRange(inputWords, 1, inputWords.length)));
					
					break;
				}

			}

			client.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
