package fibServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class fibClient {

    public static final int PORTNUMBER = 5678;
    public static final int FEHLER_EINGABE = -1;
    public static final int FEHLER_ZAHLENBEREICH = -2;
    public static String help = String.format("mögliche Befehle: \nhilfe - Bedienungshilfe wird ausgegeben \n" +
            "berechne <zahl> - berechnet fibonacci für <zahl> \n" +
            "ende - beendet die Anwendung");


    public static void main(String[] args) {
        try {

            int port;
            try{
                port = Integer.parseInt(args[1]);
                if(port < 0) throw new NumberFormatException("Port < 0");
            } catch(Exception e){
                port = PORTNUMBER;
            }

            InetAddress address;
            try{
                address = InetAddress.getByAddress(args[0].getBytes());
            } catch(Exception e){
                address = InetAddress.getLocalHost();
            }


			final Socket client = new Socket(address, port);
            final PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));


            String input;
            loop: while((input = consoleInput.readLine()) != null){
                switch(input.split(" ")[0]){
                    case "hilfe" :
                        System.out.println(help); break;
                    case "ende" :
                        System.out.println("Closing client"); break loop;
                    case "berechne" :
                        String maybeNumber;
                        try {
                            maybeNumber = input.split(" ")[1];
                        } catch(ArrayIndexOutOfBoundsException e){
                            System.out.println("command 'berechne' needs a number"); break;
                        }
                        out.println(maybeNumber);

                        String feedback;
                        int response = Integer.parseInt(in.readLine());

                        switch(response){
                            case FEHLER_EINGABE: feedback = "Fehlerhafte Eingabe"; break;
                            case FEHLER_ZAHLENBEREICH: feedback = "Ungültiger Zahlenbereich"; break;
                            default: feedback = maybeNumber + ". number in Fibonacci's sequence:" + response;
                        }
                        System.out.println(feedback); break;
                    default:
                        System.out.println("command not supported");
                }

            }

            client.close();

		} catch (IOException e) {
            e.printStackTrace();
        }
	}



}

