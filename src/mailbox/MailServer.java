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

                out.println(new Response(204,0,new String[]{"Connected to sever"}).json());

                String userInput;
                while ((userInput = in.readLine()) != null) {
                    try {
                        Gson gson = new Gson();
                        Request request = gson.fromJson(userInput, Request.class);
                        out.println(handleRequest(request));

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

    public static Response handleRequest(Request request){
        String command = request.getCommand();

        switch(command){
            case "login":

        }
        return null;
    }

    private static Response login(String username){

    }

    private class User{

        private String username;

    }
}
