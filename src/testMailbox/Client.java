package testMailbox;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import mailbox.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert on 6/20/16.
 */
public class Client {

    private Client client;
    private final int PORTNUMBER = 8090;
    private final String help = String
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

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private int lastSequence;

    public Client(){
        try {
            InetAddress address = InetAddress.getLocalHost();
            clientSocket = new Socket(address, PORTNUMBER);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            lastSequence = (int) Math.random()*10000;
            this.client = this;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void takeCommands(){
        final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Client can now take commands. ");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                try {
                    loop:while ((input = consoleInput.readLine()) != null) {
                      String[] inputWords = input.split(" ");
                      switch (inputWords[0]) {
                          case "hilfe":
                              System.out.println(help);
                              break;
                          case "ende":
                              System.out.println("Closing client");
                              break loop;
                          default:
                              int seq = client.getLastSequence();
                              client.incrementSequence();
                              out.println(new Request(seq, inputWords[0], Arrays.copyOfRange(inputWords, 1, inputWords.length)).json());
                              break;
                      }

                }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    client.getClientSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public void listenForMessages(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        String input = client.getIn().readLine();
                        if(input != null){
                            try{
                                Response response = new Gson().fromJson(input, Response.class);
                                if(response != null){
                                    if(response.getStatus() != StatusCodes.OKOHNEANTWORT){
                                        System.out.println(response.getSequence() + " " + response.getStatus() + " " + Arrays.toString(response.getData()));
                                    }
                                } else {
                                    System.out.println(input);
                                }
                            } catch(IllegalStateException | JsonSyntaxException e){
                                System.out.println(input);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }



    public int getPORTNUMBER() {
        return PORTNUMBER;
    }

    public String getHelp() {
        return help;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public int getLastSequence(){
        return lastSequence;
    }

    public void incrementSequence(){
        this.lastSequence++;
    }


    public static void main(String[] args) {
        Client client = new Client();
        client.takeCommands();
        client.listenForMessages();
    }

}
