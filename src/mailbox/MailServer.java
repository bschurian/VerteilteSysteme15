package mailbox;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MailServer {

    public static final int PORTNUMBER = 8090;
    public static final int MAX_USERS = 5;

    public static PrintWriter out;
    public static BufferedReader in;


    public static void main(String[] args) {
        // setting up the server
        try {
            int port;
            try {
                port = Integer.parseInt(args[0]);
                if (port <= 0)
                    throw new NumberFormatException("Port number < 0");
            } catch (Exception e) {
                port = PORTNUMBER;
            }

            @SuppressWarnings("resource")
            final ServerSocket server = new ServerSocket(port);
            Socket clientSocket = null;

            boolean killClientFlag;

            // setting up a connection
            connectionBuild: while (true) {
                clientSocket = server.accept();
                out = new PrintWriter(
                        clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                killClientFlag = false;

                String userInput;
                while ((userInput = in.readLine()) != null) {
                    try {
//                        int result;
//                        try {
//                            final int number = Integer.parseInt(userInput);
//                            result = 25;
//                        } catch (NumberFormatException nfe) {
//                            result = FEHLER_EINGABE;
//                            killClientFlag = true;//uncomment so server is very unforgiving
//                        } catch (IllegalArgumentException iaE) {
//                            result = FEHLER_ZAHLENBEREICH;
//                            killClientFlag = true;//uncomment so server is unforgiving
//                        }
//                        // prints to and flushes out
//                        out.println(result);
//                        //killing(closing) of client happens here if he has incurred our wrath
//                        if(killClientFlag){
//                            clientSocket.close();
//                            continue connectionBuild;
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("server is kill");
    }

    private class User{

        private String username;

    }
}
