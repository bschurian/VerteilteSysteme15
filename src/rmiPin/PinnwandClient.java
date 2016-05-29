package rmiPin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class PinnwandClient {

	public static String serviceName = "Pinnwand";
	public static final int FEHLER_EINGABE = -1;
	public static final int FEHLER_ZAHLENBEREICH = -2;
	public static String help = String
			.format("moegliche Befehle: \nhilfe - Bedienungshilfe wird ausgegeben \n"
					+ "login <Zeichenkette> - es wird versucht sich mit <zahl> bei der Pinnwand anzumelden\n"
					+ "getMessageCount - gibt die gesamte Anzahl aller Nachrichten auf der Pinnwand zurück\n"
					+ "getMessages - gibt alle Nachrichten der Pinnwand zurück\n"
					+ "getMessage <Zahl> - gibt die <Zahl>te (0-basiert) Nachrichten der Pinnwand zurück\n"
					+ "putMessage <Zeichenkette> - erstellt eine Nachricht mit der Zeichenkette <Zeichenkette> auf der Pinnwand\n"
					+ "ende - beendet die Anwendung");

	public static void main(String[] args) {

		Registry registry;

		Pinnwand pinnwand;

		try{

			registry = LocateRegistry.getRegistry(1099);

			pinnwand = 	(Pinnwand) registry.lookup(serviceName);

			final BufferedReader consoleInput = new BufferedReader(
					new InputStreamReader(System.in));
			
			System.out.println("Client can now take commands. ");
			
			// reading from console
			String input;
			try {
				loop: while ((input = consoleInput.readLine()) != null) {
					switch (input.split(" ")[0]) {
					case "hilfe":
						System.out.println(help);
						break;
					case "getMessageCount":
						System.out.println(pinnwand.getMessageCount());
						break;
					case "getMessages":
						System.out.println("Messages:");
						for(Object message: pinnwand.getMessages()){
							System.out.println("\t"+ (String)message);
						}
						break;
					case "getMessage":
						System.out.println(pinnwand.getMessage(Integer.parseInt(input.split(" ")[1])));
						break;
					case "putMessage":
						System.out.println(pinnwand.putMessage(input.trim().substring(11)));
						break;
					case "ende":
						System.out.println("Closing client");
						break loop;
					default:
						System.out.println("command not supported - use command 'hilfe' for a list of commands");
						break;
					}
				}			
			consoleInput.close();
			System.out.println("client closed");
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch(RemoteException | NotBoundException e){
			e.printStackTrace();
		}
	}
}
