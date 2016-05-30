package rmiPin;

import javax.security.auth.login.AccountException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


public class PinnwandClient {

	public static String serviceName = "Pinnwand";
	public static final int FEHLER_EINGABE = -1;
	public static final int FEHLER_ZAHLENBEREICH = -2;
	public static String help = String
			.format("moegliche Befehle: \nhilfe - Bedienungshilfe wird ausgegeben \n"
					+ "login <Zeichenkette> - es wird versucht sich mit <zahl> bei der Pinnwand anzumelden\n"
					+ "getMessageCount - gibt die gesamte Anzahl aller Nachrichten auf der Pinnwand zur�ck\n"
					+ "getMessages - gibt alle Nachrichten der Pinnwand zur�ck\n"
					+ "getMessage <Zahl> - gibt die <Zahl>te (0-basiert) Nachrichten der Pinnwand zur�ck\n"
					+ "putMessage <Zeichenkette> - erstellt eine Nachricht mit der Zeichenkette <Zeichenkette> auf der Pinnwand\n"
					+ "ende - beendet die Anwendung");

	public static void main(String[] args) {

		Registry registry;

		Pinnwand pinnwand;

		try {

			registry = LocateRegistry.getRegistry(1099);

			pinnwand = (Pinnwand) registry.lookup(serviceName);


			final BufferedReader consoleInput = new BufferedReader(
					new InputStreamReader(System.in));

			System.out.println("Client can now take commands. ");

			// reading from console
			String input;
			try {
				loop:
				while ((input = consoleInput.readLine()) != null) {
					try {
						switch (input.split(" ")[0]) {
							case "hilfe":
								System.out.println(help);
								break;
							case "login":
								int feedback = pinnwand.login(input.split(" ")[1]);
								if(feedback == 1) System.out.println("Sie haben sich erfolgreich angemeldet!");
								break;
							case "getMessageCount":
								System.out.println(pinnwand.getMessageCount());
								break;
							case "getMessages":
								Object[] messages = pinnwand.getMessages();
								if(messages.length > 0) {
									System.out.println("Messages:");
									for (Object message : messages) {
										System.out.println("\t" + (String) message);
									}
								} else {
									System.out.println("Noch keine Nachrichten");
								}
								break;
							case "getMessage":
								System.out.println(pinnwand.getMessage(Integer.parseInt(input.split(" ")[1])));
								break;
							case "putMessage":
								boolean success = pinnwand.putMessage(input.substring(10).trim());
								if(success){
									System.out.println("Nachricht erfolgreich hinzugefuegt");
								}
								break;
							case "ende":
								System.out.println("Closing client");
								break loop;
							default:
								System.out.println("command not supported - use command 'hilfe' for a list of commands");
								break;
						}
					} catch (IllegalArgumentException | AccountException e) {
						System.out.println(e.getMessage());
					}
				}
				consoleInput.close();
				System.out.println("client closed");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();

		}
	}
}
